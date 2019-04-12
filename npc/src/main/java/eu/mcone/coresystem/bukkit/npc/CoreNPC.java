/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.CoreLocation;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.NpcModule;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.bukkit.npc.util.ReflectionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class CoreNPC<T extends PlayerNpcData> implements NPC {

    private final Set<Player> spawned;

    @Getter
    protected NpcData data;
    @Getter
    private NpcVisibilityMode visibilityMode;
    @Getter
    protected Set<Player> visiblePlayersList;
    @Getter
    protected final int entityId;
    @Getter
    protected T entityData;

    protected CoreNPC(Class<T> dataClass, NpcData data, NpcVisibilityMode visibilityMode, Player... players) {
        this.spawned = new HashSet<>();
        this.data = data;
        this.visibilityMode = visibilityMode;
        this.visiblePlayersList = new HashSet<>();
        this.entityId = getNextEntityId();
        this.entityData = NpcModule.getInstance().getCoreSystem().getGson().fromJson(data.getEntityData(), dataClass);
        this.data.setEntityData(NpcModule.getInstance().getCoreSystem().getGson().toJsonTree(entityData));

        onCreate();
        toggleNpcVisibility(visibilityMode, players);
    }

    protected abstract void _spawn(Player player);

    protected abstract void _despawn(Player player);

    protected abstract void onCreate();

    protected abstract void onUpdate(T entityData);

    @Override
    public void spawn(Player p) {
        if (spawned.add(p)) {
            _spawn(p);
        }
    }

    @Override
    public void despawn(Player p) {
        if (spawned.remove(p)) {
            _despawn(p);
        }
    }

    public void playerJoined(Player... players) {
        if (visibilityMode.equals(NpcVisibilityMode.BLACKLIST)) {
            visiblePlayersList.addAll(Arrays.asList(players));
        }
    }

    public void playerLeaved(Player p) {
        visiblePlayersList.remove(p);
    }

    protected void changeDisplayname(String displayname) {
        this.data.setDisplayname(displayname);

        for (Player p : visiblePlayersList) {
            despawn(p);
            spawn(p);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(NpcData data) {
        if (this.data.getType().equals(data.getType())) {
            if (!this.data.getLocation().equals(data.getLocation())) teleport(data.getLocation().bukkit());
            if (!this.data.getDisplayname().equals(data.getDisplayname())) changeDisplayname(data.getDisplayname());

            T entityData = (T) NpcModule.getInstance().getCoreSystem().getGson().fromJson(data.getEntityData(), this.entityData.getClass());
            onUpdate(entityData);
            this.entityData = entityData;
            this.data.setEntityData(NpcModule.getInstance().getCoreSystem().getGson().toJsonTree(this.entityData));
        } else {
            throw new NpcCreateException("Could not update npc " + data.getName() + ": EntityTypes are not equal (" + this.data.getType() + " != " + data.getType() + ")");
        }
    }

    @Override
    public void toggleNpcVisibility(NpcVisibilityMode visibility, Player... players) {
        Set<Player> uuidList = new HashSet<>(Arrays.asList(players));
        Set<Player> doSet = new HashSet<>();
        Set<Player> doUnset = new HashSet<>();

        if (visibility.equals(NpcVisibilityMode.WHITELIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (uuidList.contains(player) && !visiblePlayersList.contains(player)) {
                    doSet.add(player);
                    visiblePlayersList.add(player);
                } else if (!uuidList.contains(player) && visiblePlayersList.contains(player)) {
                    doUnset.add(player);
                    visiblePlayersList.remove(player);
                }
            }
        } else if (visibility.equals(NpcVisibilityMode.BLACKLIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (uuidList.contains(player) && visiblePlayersList.contains(player)) {
                    doUnset.add(player);
                    visiblePlayersList.remove(player);
                } else if (!uuidList.contains(player) && !visiblePlayersList.contains(player)) {
                    doSet.add(player);
                    visiblePlayersList.add(player);
                }
            }
        }

        for (Player p : doSet) {
            spawn(p);
        }
        for (Player p : doUnset) {
            despawn(p);
        }

        visiblePlayersList = uuidList;
    }

    @Override
    public void toggleVisibility(Player player, boolean canSee) {
        if (canSee && !visiblePlayersList.contains(player)) {
            _spawn(player);
            visiblePlayersList.add(player);
        } else if (visiblePlayersList.contains(player)) {
            _despawn(player);
            visiblePlayersList.remove(player);
        }
    }

    public boolean isVisibleFor(Player player) {
        return visiblePlayersList.contains(player);
    }

    @Override
    public boolean canBeSeenBy(Player player) {
        if (player.getLocation().getWorld().equals(data.getLocation().bukkit().getWorld())) {
            return player.getLocation().distanceSquared(data.getLocation().bukkit())
                    < (Bukkit.spigot().getConfig().getInt("world-settings.default.entity-tracking-range.players", 48) * 24);
        } else {
            return false;
        }
    }

    @Override
    public void sendState(NpcState state) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        ReflectionManager.setValue(packet, "a", entityId);
        ReflectionManager.setValue(packet, "b", (byte) state.getId());
        sendPackets(packet);
    }

    @Override
    public void sendAnimation(NpcAnimation animation) {
        sendPackets(makeAnimationPacket(animation));
    }

    @Override
    public void teleport(Location loc) {
        CoreLocation cloc = new CoreLocation(loc);

        sendPackets(makeTeleportPackets(cloc));
        this.data.setLocation(cloc);
    }

    @Override
    public void teleport(CoreLocation loc) {
        sendPackets(makeTeleportPackets(loc));
        this.data.setLocation(loc);
    }

    private static synchronized int getNextEntityId() {
        try {
            Field field = Entity.class.getDeclaredField("entityCount");
            field.setAccessible(true);
            int id = field.getInt(null);
            field.set(null, id + 1);
            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    protected void sendPackets(Packet<?>... packets) {
        for (Player p : visiblePlayersList) {
            sendPackets(p, packets);
        }
    }

    protected void sendPackets(Player p, Packet<?>... packets) {
        boolean multipleInfoSend = false;

        for (Packet<?> packet : packets) {
            if (packet != null) {
                if (packet instanceof PacketPlayOutPlayerInfo) {
                    if (multipleInfoSend) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) NpcModule.getInstance().getCoreSystem(), () ->
                                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet), 50);
                        continue;
                    } else {
                        multipleInfoSend = true;
                    }
                }

                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    protected Packet<?>[] makeTeleportPackets(CoreLocation loc) {
        if (!loc.getWorld().equals(data.getLocation().getWorld())) {
            throw new IllegalArgumentException("Locations are not in the same world.");
        }

        return new Packet[]{
                new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                        entityId,
                        (byte) ((loc.getX() - data.getLocation().getX()) * 32),
                        (byte) ((loc.getY() - data.getLocation().getX()) * 32),
                        (byte) ((loc.getZ() - data.getLocation().getX()) * 32),
                        (byte) (loc.getYaw() * 256F / 360F),
                        (byte) (loc.getPitch() * 256F / 360F),
                        false
                ),
                new PacketPlayOutEntityTeleport(
                        entityId,
                        (int) (loc.getX() * 32),
                        (int) (loc.getY() * 32),
                        (int) (loc.getZ() * 32),
                        (byte) (loc.getYaw() * 256F / 360F),
                        (byte) (loc.getPitch() * 256F / 360F),
                        false
                ),
                makeHeadRotationPacket(loc.getYaw())
        };
    }

    protected PacketPlayOutEntityHeadRotation makeHeadRotationPacket(float yaw) {
        PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        ReflectionManager.setValue(packet, "a", entityId);
        ReflectionManager.setValue(packet, "b", (byte) (yaw * 256F / 360F));

        return packet;
    }

    protected PacketPlayOutAnimation makeAnimationPacket(NpcAnimation animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        ReflectionManager.setValue(packet, "a", entityId);
        ReflectionManager.setValue(packet, "b", animation.getId());

        return packet;
    }

}

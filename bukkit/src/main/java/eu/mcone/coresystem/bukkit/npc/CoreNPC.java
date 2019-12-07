/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.util.ReflectionManager;
import eu.mcone.coresystem.bukkit.util.PlayerListModeToggleUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public abstract class CoreNPC<T extends AbstractNpcData> extends PlayerListModeToggleUtil implements NPC {

    private final Set<Player> spawned;

    @Getter
    protected NpcData data;
    @Getter
    protected final int entityId;
    @Getter
    protected T entityData;

    protected CoreNPC(Class<T> dataClass, NpcData data, ListMode listMode, Player... players) {
        this.spawned = new HashSet<>();
        this.data = data;
        this.visiblePlayersList = new HashSet<>();
        this.entityId = getNextEntityId();
        this.entityData = BukkitCoreSystem.getInstance().getGson().fromJson(data.getEntityData(), dataClass);
        this.data.setEntityData(BukkitCoreSystem.getInstance().getGson().toJsonTree(entityData));

        onCreate();
        togglePlayerVisibility(listMode, players);
    }

    protected abstract void _spawn(Player player);

    protected abstract void _despawn(Player player);

    protected abstract void onCreate();

    protected abstract void onUpdate(T entityData);


    public void spawn(Player player) {
        if (spawned.add(player)) {
            _spawn(player);
        }
    }

    public void despawn(Player player) {
        if (spawned.remove(player)) {
            _despawn(player);
        }
    }

    @Override
    public void changeDisplayname(String displayname, Player... players) {
        if (players.length > 0) {
            this.data.setDisplayname(displayname);
        } else {
            players = visiblePlayersList.toArray(new Player[0]);
        }

        for (Player p : players) {
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

            T entityData = (T) BukkitCoreSystem.getInstance().getGson().fromJson(data.getEntityData(), this.entityData.getClass());
            onUpdate(entityData);
            this.entityData = entityData;
            this.data.setEntityData(BukkitCoreSystem.getInstance().getGson().toJsonTree(this.entityData));
        } else {
            throw new NpcCreateException("Could not update npc " + data.getName() + ": EntityTypes are not equal (" + this.data.getType() + " != " + data.getType() + ")");
        }
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
    public Set<Player> getVisiblePlayersList() {
        return new HashSet<>(visiblePlayersList);
    }

    @Override
    public void sendState(NpcState state, Player... players) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        ReflectionManager.setValue(packet, "a", entityId);
        ReflectionManager.setValue(packet, "b", (byte) state.getId());

        sendPackets(packet, players);
    }

    @Override
    public void sendAnimation(NpcAnimation animation, Player... players) {
        sendPackets(makeAnimationPacket(animation), players);
    }

    @Override
    public void teleport(Location loc, Player... players) {
        teleport(new CoreLocation(loc), players);
    }

    @Override
    public void teleport(CoreLocation location, Player... players) {
        sendPackets(makeTeleportPackets(location), players);

        if (players.length > 0) {
            this.data.setLocation(location);
        }
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

    public void sendPackets(Packet<?>... packets) {
        sendPackets(packets, visiblePlayersList);
    }

    protected void sendPackets(Packet<?> packet, Player[] players) {
        sendPackets(new Packet[]{packet}, players);
    }

    protected void sendPackets(Packet<?>[] packets, Player... players) {
        players = players.length > 0 ? players : visiblePlayersList.toArray(new Player[0]);

        for (Player p : players) {
            sendPackets(p, packets);
        }
    }

    protected void sendPackets(Packet<?>[] packets, Set<Player> players) {
        players = players.size() > 0 ? players : visiblePlayersList;

        for (Player p : players) {
            sendPackets(p, packets);
        }
    }

    protected void sendPackets(Player p, Packet<?>... packets) {
        boolean multipleInfoSend = false;

        for (Packet<?> packet : packets) {
            if (packet != null) {
                if (packet instanceof PacketPlayOutPlayerInfo) {
                    if (multipleInfoSend) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(BukkitCoreSystem.getInstance(), () ->
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

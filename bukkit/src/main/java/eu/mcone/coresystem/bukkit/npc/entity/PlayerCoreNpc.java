/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.core.exception.MotionCaptureCurrentlyRunningException;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.capture.MotionPlayer;
import eu.mcone.coresystem.bukkit.util.ReflectionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCoreNpc extends CoreNPC<PlayerNpcData> implements PlayerNpc {

    private static final Random UUID_RANDOM = new Random();
    private final Location bedLocation;

    @Getter
    private UUID uuid;
    @Getter
    private SkinInfo skin;
    @Getter
    private GameProfile profile;
    @Getter
    private Location location;
    @Getter
    private MotionPlayer motionPlayer;

    protected PlayerCoreNpc(NpcData data, ListMode visibilityMode, Player[] players) {
        super(PlayerNpcData.class, data, visibilityMode, players);
        this.bedLocation = new Location(data.getLocation().bukkit().getWorld(), 1, 1, 1);
        this.location = data.getLocation().bukkit();
    }

    @Override
    protected void onCreate() {
        this.uuid = new UUID(UUID_RANDOM.nextLong(), 0);

        try {
            switch (entityData.getSkinType()) {
                case CUSTOM:
                case DATABASE:
                    this.skin = BukkitCoreSystem.getInstance().getPlayerUtils().getSkinFromSkinDatabase(entityData.getSkinName());
                    break;
                case PLAYER:
                    this.skin = BukkitCoreSystem.getInstance().getPlayerUtils().getSkinInfo(entityData.getSkinName());
                    break;
            }
        } catch (SkinNotFoundException e) {
            this.skin = new SkinInfo(
                    "MHF_Question",
                    "eyJ0aW1lc3RhbXAiOjE1MTIyNjI0NTg5NDcsInByb2ZpbGVJZCI6ImQxY2VjOWFkMWRhODQxNzliMWU1NjA0ZjcyYmZiMjI2IiwicHJvZmlsZU5hbWUiOiJydXRnZXI0NjUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQyNDQzNjg4ZGI5NjQ5YjA1NTliNjg4ZTdjMTI3ZjY2N2FiYjhmOWY1YTU5ZDVhOWRhNzEyMjZmNzNmODMzMCJ9fX0=",
                    "iVxHKJfmQh8VlCXF0Wwr6Yl6p81+OIXuGLoM/KM1sgM+OQ2WwHr7V6FNHATnlz1uGEDpzMZUKsUq9SVy50soQ9RWb7iynfB51QBxU0NByUCNQf2olHXEZH6CQEUbZTgYrN1aNZ1Y/Z7T1xrMKfc43HqNfM2H49JufRoLr4ZPWcl6T8d0lSzshndkFZxtS45PRQDBzo0QFyG63WjeoMbW42ufaTWVYz354BhnksAQWb1lSfdXcB7JQOKjf0MzeYmx0pOMB5CWhERJQZpi5mJ1MoabeSwNygcdfZAMB9xtmScbX5tUPvrC2Ooo20jl2fQ/KG+6obZeydKKr2vznj+0oq+04VnmqooHhLjXyZSvGIB3Ht9aDL9MzVbwpbLnLjrngyUzX/7+oJEm2a4xrktszoHGKHdnbD1d9CDyWh+FnyRCmO5RE3ZR8yHE5SakTKOkASR2H79RAHn+wF1h535wJctjoHyYBRc/gXWo58jyjG1hGu/6cI2XuQCEvdnkZTj0rqKWUtn8pWn2KLbm2S2S6CoTMTsGHG0anz4c4Jkzni91wzWvg5xIf1f3YzMRhuyOirtM9gyJok2BplRaGiv1f63fWa7cbS6A3nj/sKr2SNQ6Efri6+Z1xPOOy7xHFaZH0wlf8kz1qPDqIw/nAy0eZAthPN61TZw5iCegIG01KqU=",
                    SkinInfo.SkinType.PLAYER
            );

            throw new NpcCreateException("Could not create NPC: ", e);
        }

        this.profile = makeGameProfile(uuid, data.getDisplayname(), skin);
    }

    @Override
    protected void onUpdate(PlayerNpcData entityData) {
        if (!this.entityData.getSkinType().equals(entityData.getSkinType()) || !this.entityData.getSkinName().equals(entityData.getSkinName())) {
            SkinInfo skin = null;

            try {
                switch (entityData.getSkinType()) {
                    case CUSTOM:
                    case DATABASE:
                        skin = BukkitCoreSystem.getInstance().getPlayerUtils().getSkinFromSkinDatabase(entityData.getSkinName());
                        break;
                    case PLAYER:
                        skin = BukkitCoreSystem.getInstance().getPlayerUtils().getSkinInfo(entityData.getSkinName());
                        break;
                }

                setSkin(skin);
            } catch (SkinNotFoundException e) {
                e.printStackTrace();
            }
        }

        setVisibleOnTab(entityData.isVisibleOnTab());

        if (this.entityData.isSleeping() != entityData.isSleeping()) {
            if (entityData.isSleeping()) {
                setSleeping(entityData.isSleepWithBed());
            } else {
                setAwake();
            }
        } else if (this.entityData.isSleepWithBed() != entityData.isSleepWithBed() && entityData.isSleeping()) {
            setAwake();
            setSleeping(entityData.isSleepWithBed());
        }

        if (!this.entityData.getEquipment().equals(entityData.getEquipment())) {
            for (int i = 0; i < entityData.getEquipment().size() && i < 5; i++) {
                if (!this.entityData.getEquipment().get(i).equals(entityData.getEquipment().get(i))) {
                    this.entityData.getEquipment().set(i, entityData.getEquipment().get(i));
                    sendPackets(makeEquipmentPacket(i));
                }
            }
        }
    }

    public void playMotionCapture(final String name) {
        MotionCaptureData data = CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getMotionCapture(name);
        if (data != null) {
            playMotionCapture(data);
        }
    }

    public void playMotionCapture(final MotionCaptureData data) {
        try {
            if (motionPlayer != null) {
                if (motionPlayer.isPlaying()) {
                    throw new MotionCaptureCurrentlyRunningException();
                } else {
                    motionPlayer = new MotionPlayer(this, data);
                    motionPlayer.playMotionCapture();
                }
            } else {
                motionPlayer = new MotionPlayer(this, data);
                motionPlayer.playMotionCapture();
            }
        } catch (MotionCaptureCurrentlyRunningException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeDisplayname(String displayname, Player... players) {
        this.profile = makeGameProfile(uuid, displayname, skin);
        super.changeDisplayname(displayname, players);

        if (players.length == 0) {
            this.profile = makeGameProfile(uuid, data.getDisplayname(), skin);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void _spawn(Player p) {
        int size = entityData.isVisibleOnTab() ? 8 : 9;
        size += entityData.isSleeping() ? 4 : 0;

        Packet<?>[] packets = new Packet[size];

        packets[0] = makeTablistPacket(true);

        packets[1] = new PacketPlayOutNamedEntitySpawn();
        ReflectionManager.setValue(packets[1], "a", entityId);
        ReflectionManager.setValue(packets[1], "b", profile.getId());
        ReflectionManager.setValue(packets[1], "c", MathHelper.floor(data.getLocation().getX() * 32.0D));
        ReflectionManager.setValue(packets[1], "d", MathHelper.floor(data.getLocation().getY() * 32.0D));
        ReflectionManager.setValue(packets[1], "e", MathHelper.floor(data.getLocation().getZ() * 32.0D));
        ReflectionManager.setValue(packets[1], "f", (byte) ((int) (data.getLocation().getYaw() * 256.0F / 360.0F)));
        ReflectionManager.setValue(packets[1], "g", (byte) ((int) (data.getLocation().getPitch() * 256.0F / 360.0F)));
        ReflectionManager.setValue(packets[1], "h", 0);
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0);
        watcher.a(6, (float) 20);
        watcher.a(10, (byte) 127);
        ReflectionManager.setValue(packets[1], "i", watcher);

        packets[2] = makeHeadRotationPacket(data.getLocation().getYaw());

        for (int i = 0; i < entityData.getEquipment().size() && i < 5; i++) {
            packets[i + 3] = makeEquipmentPacket(i);
        }

        if (!entityData.isVisibleOnTab()) {
            packets[8] = makeTablistPacket(false);
        }

        if (entityData.isSleeping()) {
            p.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);

            if (!entityData.isSleepWithBed()) {
                data.getLocation().add(0, 0.15, 0);
            }

            Packet<?>[] sleepPackets = makeSleepPackets(bedLocation);
            packets[9] = sleepPackets[0];
            packets[10] = sleepPackets[1];
            packets[11] = sleepPackets[2];
            packets[12] = sleepPackets[3];
        }

        sendPackets(p, packets);
    }

    @Override
    protected void _despawn(Player player) {
        sendPackets(
                player,
                makeTablistPacket(false),
                new PacketPlayOutEntityDestroy(entityId)
        );
    }

    @Override
    public void setEquipment(EquipmentPosition position, ItemStack item, Player... players) {
        entityData.getEquipment().set(position.getId(), item);
        sendPackets(makeEquipmentPacket(position.getId()), players);
    }

    @Override
    public void setSkin(SkinInfo skin, Player... players) {
        this.skin = skin;

        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        players = players.length > 0 ? players : visiblePlayersList.toArray(new Player[0]);
        for (Player p : players) {
            _despawn(p);
            _spawn(p);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setSleeping(boolean sleepWithBed) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);
        }

        if (!sleepWithBed) {
            data.getLocation().add(0, 0.15, 0);
        }

        sendPackets(makeSleepPackets(bedLocation));
    }

    @Override
    public void setAwake() {
        sendAnimation(NpcAnimation.LEAVE_BED);

        if (!entityData.isSleepWithBed()) {
            data.getLocation().subtract(0, 0.15, 0);
        }
        teleport(data.getLocation());
    }

    @Override
    public void setTablistName(String name, Player... players) {
        if (!entityData.getTablistName().equals(name)) {
            if (entityData.isVisibleOnTab()) {
                sendPackets(new Packet[]{makeTablistPacket(false), makeTablistPacket(true)}, players);
            }
            entityData.setTablistName(name);
        }
    }

    @Override
    public void setVisibleOnTab(boolean visible, Player... players) {
        if (entityData.isVisibleOnTab() != visible) {
            sendPackets(makeTablistPacket(visible), players);
            entityData.setVisibleOnTab(visible);
        }
    }

    @Override
    public void playLabymodEmote(int emoteId, Player... players) {
        JsonArray array = new JsonArray();

        JsonObject emote = new JsonObject();
        emote.addProperty("uuid", uuid.toString());
        emote.addProperty("emote_id", emoteId);
        array.add(emote);

        Collection<? extends Player> send = players.length > 0 ? Arrays.asList(players) : Bukkit.getOnlinePlayers();
        for (Player player : send) {
            BukkitCoreSystem.getInstance().getLabyModAPI().sendServerMessage(player, "emote_api", array);
        }
    }

    public void sneak(boolean sneak) {
        DataWatcher w = new DataWatcher(null);
        if (sneak) {
            w.a(0, (byte) 2);
        } else {
            w.a(0, (byte) 0);
        }

        w.a(1, (short) 0);
        w.a(8, (byte) 0);

        sendPackets(new PacketPlayOutEntityMetadata(entityId, w, true));
    }

    //Crashes the client, because float cannot be cast to byte (Minecraft Client error stacktrace)
    public void block(final boolean block) {
        DataWatcher w = new DataWatcher(null);
        w.a(0, (byte) 16);
        w.a(1, (short) 0);

        if (block) {
            w.a(6, (byte) 1);
        } else {
            w.a(6, (byte) 0);
        }

        sendPackets(new PacketPlayOutEntityMetadata(entityId, w, true));
    }

    public void setItemInHand(final ItemStack item) {
        sendPackets(new PacketPlayOutEntityEquipment(entityId, 0, CraftItemStack.asNMSCopy(item)));
    }

    private PacketPlayOutPlayerInfo makeTablistPacket(boolean add) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionManager.setValue(packet, "a", add ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, CraftChatMessage.fromString(entityData.getTablistName())[0])
        )));

        return packet;
    }

    private PacketPlayOutEntityEquipment makeEquipmentPacket(int slot) {
        return entityData.getEquipment().get(slot) != null ? new PacketPlayOutEntityEquipment(entityId, slot, CraftItemStack.asNMSCopy(entityData.getEquipment().get(slot))) : null;
    }

    private Packet<?>[] makeSleepPackets(Location bedLocation) {
        BlockPosition block = new BlockPosition(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ());

        PacketPlayOutBed bedPacket = new PacketPlayOutBed();
        ReflectionManager.setValue(bedPacket, "a", entityId);
        ReflectionManager.setValue(bedPacket, "b", block);

        Packet<?>[] tpPackets = makeTeleportPackets(data.getLocation());

        return new Packet[]{
                bedPacket,
                tpPackets[0],
                tpPackets[1],
                tpPackets[2]
        };
    }

    private GameProfile makeGameProfile(UUID uuid, String name, SkinInfo skin) {
        GameProfile profile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', name));
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        return profile;
    }

}

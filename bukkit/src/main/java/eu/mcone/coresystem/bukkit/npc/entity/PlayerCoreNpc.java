/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
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
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCoreNpc extends CoreNPC<PlayerNpcData> implements PlayerNpc {

    private static final Random UUID_RANDOM = new Random();

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
    private Map<Integer, DataWatcher.Item<?>> options;

    protected PlayerCoreNpc(NpcData data, ListMode visibilityMode, Player[] players) {
        super(PlayerNpcData.class, data, visibilityMode, players);
        this.location = data.getLocation().bukkit();
    }

    @Override
    protected void onCreate() {
        this.uuid = new UUID(UUID_RANDOM.nextLong(), 0);
        this.options = new HashMap<>();
        this.options.put(16, new DataWatcher.Item<>(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 0xFF));

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
            this.entityData.setEquipment(new HashMap<>(entityData.getEquipment()));

            for (Map.Entry<EnumItemSlot, ItemStack> equipment : entityData.getEquipment().entrySet()) {
                sendPackets(makeEquipmentPacket(equipment.getKey(), equipment.getValue()));
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
                    motionPlayer.play();
                }
            } else {
                motionPlayer = new MotionPlayer(this, data);
                motionPlayer.play();
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
        int size = entityData.isVisibleOnTab() ? 10 : 11;
        int i = -1;
        Packet<?>[] packets = new Packet[size];

        if (entityData.isSleeping()) {
            this.options.put(6, new DataWatcher.Item<>(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.SLEEPING));

            if (!entityData.isSleepWithBed()) {
                data.getLocation().add(0, 0.15, 0);
            }
        }

        packets[++i] = makeTablistPacket(true);

        packets[++i] = new PacketPlayOutNamedEntitySpawn();
        ReflectionManager.setValue(packets[i], "a", entityId);
        ReflectionManager.setValue(packets[i], "b", profile.getId());
        ReflectionManager.setValue(packets[i], "c", data.getLocation().getX());
        ReflectionManager.setValue(packets[i], "d", data.getLocation().getY());
        ReflectionManager.setValue(packets[i], "e", data.getLocation().getZ());
        ReflectionManager.setValue(packets[i], "f", (byte) ((int) (data.getLocation().getYaw() * 256.0F / 360.0F)));
        ReflectionManager.setValue(packets[i], "g", (byte) ((int) (data.getLocation().getPitch() * 256.0F / 360.0F)));

        packets[++i] = makeMetadataPacket();

        packets[++i] = makeHeadRotationPacket(data.getLocation().getYaw());

        for (Map.Entry<EnumItemSlot, ItemStack> equipment : entityData.getEquipment().entrySet()) {
            packets[++i] = makeEquipmentPacket(equipment.getKey(), equipment.getValue());
        }

        if (!entityData.isVisibleOnTab()) {
            packets[++i] = makeTablistPacket(false);
        }

        System.out.println(Arrays.toString(packets));
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
    public void setEquipment(EnumItemSlot slot, ItemStack item, Player... players) {
        entityData.getEquipment().put(slot, item);
        sendPackets(makeEquipmentPacket(slot, item), players);
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

    @Override
    public void setSleeping(boolean sleepWithBed) {
        if (!sleepWithBed) {
            data.getLocation().add(0, 0.15, 0);
        }

        this.options.put(6, new DataWatcher.Item<>(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.SLEEPING));

        Packet<?>[] tpPackets = makeTeleportPackets(data.getLocation());
        sendPackets(makeMetadataPacket(), tpPackets[0], tpPackets[1]);
    }

    @Override
    public void setAwake() {
        if (!entityData.isSleepWithBed()) {
            data.getLocation().subtract(0, 0.15, 0);
        }

        this.options.put(6, new DataWatcher.Item<>(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.STANDING));
        Packet<?>[] tpPackets = makeTeleportPackets(data.getLocation());
        sendPackets(makeMetadataPacket(), tpPackets[0], tpPackets[1]);
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

    /*@Override
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
    }*/

    @Override
    public void sneak(boolean sneak, Player... players) {
        this.options.put(6, new DataWatcher.Item<>(new DataWatcherObject<>(6, DataWatcherRegistry.s), sneak ? EntityPose.CROUCHING : EntityPose.STANDING));
        sendPackets(makeMetadataPacket(), players);
    }

    private PacketPlayOutPlayerInfo makeTablistPacket(boolean add) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionManager.setValue(packet, "a", add ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                packet.new PlayerInfoData(profile, 0, EnumGamemode.SURVIVAL, CraftChatMessage.fromString(entityData.getTablistName())[0])
        )));

        return packet;
    }

    private PacketPlayOutEntityEquipment makeEquipmentPacket(EnumItemSlot slot, ItemStack item) {
        return new PacketPlayOutEntityEquipment(entityId, slot, CraftItemStack.asNMSCopy(item));
    }

    private PacketPlayOutEntityMetadata makeMetadataPacket() {
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        ReflectionManager.setValue(packet, "a", entityId);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(this.options.values()));

        return packet;
    }

    private GameProfile makeGameProfile(UUID uuid, String name, SkinInfo skin) {
        GameProfile profile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', name));
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        return profile;
    }

}

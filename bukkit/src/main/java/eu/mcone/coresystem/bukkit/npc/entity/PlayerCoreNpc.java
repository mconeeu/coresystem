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
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.util.CoreProjectile;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.core.exception.MotionCaptureCurrentlyRunningException;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.capture.MotionPlayer;
import eu.mcone.coresystem.bukkit.npc.nms.EntityHumanNPC;
import eu.mcone.coresystem.bukkit.util.ReflectionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerCoreNpc extends CoreNPC<EntityHumanNPC, PlayerNpcData> implements PlayerNpc {

    private static final Random UUID_RANDOM = new Random();
    private final Location bedLocation;

    @Getter
    private UUID uuid;
    @Getter
    private SkinInfo skin;
    @Getter
    private GameProfile profile;
    @Getter
    private MotionPlayer motionPlayer;
    private DataWatcher dataWatcher;

    public PlayerCoreNpc(NpcData data, ListMode visibilityMode, Player[] players) {
        super(PlayerNpcData.class, data, visibilityMode, players);
        this.bedLocation = new Location(location.getWorld(), 1, 1, 1);
    }

    @Override
    protected void onCreate() {
        this.uuid = new UUID(UUID_RANDOM.nextLong(), 0);
        this.dataWatcher = new DataWatcher(null);
        this.dataWatcher.a(10, (byte) 127);

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
                    SkinInfo.SkinType.CUSTOM
            );

            throw new NpcCreateException("Could not create NPC: ", e);
        }

        this.profile = makeGameProfile(uuid, data.getDisplayname(), skin);

        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        this.entity = new EntityHumanNPC(nmsWorld.getServer().getServer(), nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        entity.getWorld().players.add(entity);
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

            for (Map.Entry<EquipmentPosition, ItemStack> equipment : entityData.getEquipment().entrySet()) {
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
        int size = entityData.isVisibleOnTab() ? 8 : 9;
        size += entityData.isSleeping() ? 1 : 0;
        int i = -1;

        if (entityData.isSleeping() && !entityData.isSleepWithBed()) {
            location.add(0, 0.15, 0);
        }

        Packet<?>[] packets = new Packet[size];

        packets[++i] = makeTablistPacket(true);

        packets[++i] = new PacketPlayOutNamedEntitySpawn();
        ReflectionManager.setValue(packets[i], "a", entity.getId());
        ReflectionManager.setValue(packets[i], "b", profile.getId());
        ReflectionManager.setValue(packets[i], "c", MathHelper.floor(location.getX() * 32.0D));
        ReflectionManager.setValue(packets[i], "d", MathHelper.floor(location.getY() * 32.0D));
        ReflectionManager.setValue(packets[i], "e", MathHelper.floor(location.getZ() * 32.0D));
        ReflectionManager.setValue(packets[i], "f", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionManager.setValue(packets[i], "g", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        ReflectionManager.setValue(packets[i], "h", 0);
        ReflectionManager.setValue(packets[i], "i", dataWatcher);

        packets[++i] = makeHeadRotationPacket(location.getYaw());

        for (Map.Entry<EquipmentPosition, ItemStack> equipment : entityData.getEquipment().entrySet()) {
            packets[++i] = makeEquipmentPacket(equipment.getKey(), equipment.getValue());
        }

        if (!entityData.isVisibleOnTab()) {
            packets[++i] = makeTablistPacket(false);
        }

        if (entityData.isSleeping()) {
            p.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);
            BlockPosition block = new BlockPosition(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ());

            PacketPlayOutBed bedPacket = new PacketPlayOutBed();
            ReflectionManager.setValue(bedPacket, "a", entity.getId());
            ReflectionManager.setValue(bedPacket, "b", block);

            packets[++i] = bedPacket;
        }

        sendPackets(p, packets);
    }

    @Override
    protected void _despawn(Player player) {
        sendPackets(
                player,
                makeTablistPacket(false),
                new PacketPlayOutEntityDestroy(entity.getId())
        );
    }

    @Override
    public void setEquipment(EquipmentPosition position, ItemStack item, Player... players) {
        entityData.getEquipment().put(position, item);
        sendPackets(makeEquipmentPacket(position, item), players);
    }

    @Override
    public void clearEquipment() {
        entityData.getEquipment().clear();

        for (int i = 0; i < 3; i++)
            sendPackets(new PacketPlayOutEntityEquipment(entity.getId(), i, CraftItemStack.asNMSCopy(new ItemBuilder(Material.AIR, 1).create())));
        sendPackets();
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
            location.add(0, 0.15, 0);
        }

        sendPackets(makeSleepPackets(bedLocation));
    }

    @Override
    public void setAwake() {
        sendAnimation(NpcAnimation.LEAVE_BED);

        if (!entityData.isSleepWithBed()) {
            location.subtract(0, 0.15, 0);
        }
        teleport(location);
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

    @Override
    public void setBow(boolean drawBow, Player... players) {
        dataWatcher = new DataWatcher(null);
        dataWatcher.a(0, (drawBow ? (byte) 16 : (byte) 0));
        sendPackets(makeMetadataPacket(), players);
    }

    public void fishingHook(boolean hook, Player... players) {
        Location eye = new Location(location.getWorld(), location.getX(), location.getY() + 1.5, location.getZ());
        World world = ((CraftWorld) location.getWorld()).getHandle();

//        EntityFishingHook entity = new EntityFishingHook(world, this.entity);
//        entity.setPosition(location.getX(), location.getY(), location.getZ());
//        world.addEntity(entity);
//        entity.getBukkitEntity();

        this.entity.locX = location.getX();
        this.entity.locY = location.getY();
        this.entity.locZ = location.getZ();
        this.entity.yaw = location.getYaw();

        FishingHook hook1 = new FishingHook(location, this.entity);
        hook1.spawn();

//        EntityFishingHook nmsHook = new EntityFishingHook(((CraftWorld) location.getWorld()).getHandle(), this.entity);
//        nmsHook.yaw = location.getYaw();
//        nmsHook.pitch = location.getPitch();
//        nmsHook.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
//
////        nmsHook.setPosition(location.getX() + 10, location.getY() + 10, location.getZ() + 10);
//        Vector vector = location.getDirection();
//        nmsHook.g(vector.getX(), vector.getY(), vector.getZ());
////        nmsHook.setLocation(location.getX() + eye.getX(), eye.getY() + eye.getDirection().getY(), eye.getZ() + eye.getZ(), eye.getYaw(), eye.getPitch());
////        nmsHook.setLocation(location.getX() + 100, eye.getY() + 100, eye.getZ() + + 100, eye.getYaw(), eye.getPitch());
//        world.addEntity(nmsHook);
//        CraftEntity hookE = nmsHook.getBukkitEntity();
//        hookE.setVelocity(location.getDirection());

        //OLD
//        Vector vector = location.getDirection().multiply(1.5);
//        entity.getBukkitEntity().setVelocity(vector);

//        EntityFishingHook entity = new EntityFishingHook(((CraftWorld) location.getWorld()).getHandle(), this.entity);
//        entity.getBukkitEntity();
//        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity);
//        Vector vector = location.getDirection().multiply(0.8);
//        entity.getBukkitEntity().setVelocity(vector);

//        PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entity, 90);
//        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entity.getId(), vector.getX(), vector.getY(), vector.getZ());
//        sendPackets(new Packet[] {spawn, velocity}, players);
    }

    @Override
    public void sneak(boolean sneak, Player... players) {
        dataWatcher = new DataWatcher(null);
        dataWatcher.a(0, (sneak ? (byte) 2 : (byte) 0));
        dataWatcher.a(1, (short) 0);
        dataWatcher.a(8, (byte) 0);

        sendPackets(makeMetadataPacket(), players);
    }

    //Crashes the client, because float cannot be cast to byte (Minecraft Client error stacktrace)
    @Override
    public void block(boolean block, Player... players) {
        dataWatcher.a(0, (byte) 16);
        dataWatcher.a(1, (short) 0);
        dataWatcher.a(6, (block ? (byte) 1 : (byte) 0));

        sendPackets(makeMetadataPacket(), players);
    }

    public void setItemInHand(final ItemStack item, final Player... players) {
        entity.getBukkitEntity().setItemInHand(item);
        sendPackets(new PacketPlayOutEntityEquipment(entity.getId(), 0, CraftItemStack.asNMSCopy(item)), players);
    }

    //TODO: Check if this works (Code snipped: https://dev-tek.de/forum/thread/328-packetplayoutentityeffect/)
    public void addPotionEffect(MobEffect effect, Player... players) {
        PacketPlayOutEntityEffect packet = new PacketPlayOutEntityEffect();
        ReflectionManager.setValue(packet, "a", entity.getId());
        ReflectionManager.setValue(packet, "b", (byte) effect.getEffectId());
        ReflectionManager.setValue(packet, "c", (short) effect.getDuration());
        ReflectionManager.setValue(packet, "d", (byte) effect.getAmplifier());
//                plugin.reflect.setPrivateField(packet, "e", hide);
        sendPackets(packet, players);
    }

    @Override
    public CoreProjectile throwProjectile(EntityProjectile type, Vector vector) {
        return CoreSystem.getInstance().createProjectile(type).velocity(vector).throwProjectile(location.clone().add(0, 1.5, 0));
    }

    private PacketPlayOutPlayerInfo makeTablistPacket(boolean add) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionManager.setValue(packet, "a", add ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, CraftChatMessage.fromString(entityData.getTablistName())[0])
        )));

        return packet;
    }

    private PacketPlayOutEntityEquipment makeEquipmentPacket(EquipmentPosition slot, ItemStack itemStack) {
        return new PacketPlayOutEntityEquipment(entity.getId(), slot.getId(), CraftItemStack.asNMSCopy(itemStack));
    }

    private Packet<?>[] makeSleepPackets(Location bedLocation) {
        BlockPosition block = new BlockPosition(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ());

        PacketPlayOutBed bedPacket = new PacketPlayOutBed();
        ReflectionManager.setValue(bedPacket, "a", entity.getId());
        ReflectionManager.setValue(bedPacket, "b", block);

        Packet<?>[] tpPackets = makeTeleportPackets(new CoreLocation(location));

        return new Packet[]{
                bedPacket,
                tpPackets[0],
                tpPackets[1]
        };
    }

    private PacketPlayOutEntityMetadata makeMetadataPacket() {
        return new PacketPlayOutEntityMetadata(entity.getId(), dataWatcher, true);
    }

    private GameProfile makeGameProfile(UUID uuid, String name, SkinInfo skin) {
        GameProfile profile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', name));
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        return profile;
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcModule;
import eu.mcone.coresystem.bukkit.npc.util.ReflectionManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class PlayerCoreNpc extends CoreNPC<PlayerNpcData> implements PlayerNpc {

    private final Location bedLocation;

    @Getter
    private SkinInfo skin;
    @Getter
    private GameProfile profile;

    protected PlayerCoreNpc(NpcData data, NpcVisibilityMode visibilityMode, Player[] players) {
        super(PlayerNpcData.class, data, visibilityMode, players);
        this.bedLocation = new Location(data.getLocation().bukkit().getWorld(), 1, 1, 1);
    }

    @Override
    protected void onCreate() {
        try {
            switch (entityData.getSkinType()) {
                case CUSTOM:
                case DATABASE:
                    this.skin = NpcModule.getInstance().getCoreSystem().getPlayerUtils().getSkinFromSkinDatabase(entityData.getSkinName());
                    break;
                case PLAYER:
                    this.skin = NpcModule.getInstance().getCoreSystem().getPlayerUtils().getSkinInfo(entityData.getSkinName());
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

        this.profile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', data.getDisplayname()));
        this.profile.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
    }

    @Override
    protected void onUpdate(PlayerNpcData entityData) {
        if (!this.entityData.getSkinType().equals(entityData.getSkinType()) || !this.entityData.getSkinName().equals(entityData.getSkinName())) {
            SkinInfo skin = null;

            try {
                switch (entityData.getSkinType()) {
                    case CUSTOM:
                    case DATABASE:
                        skin = NpcModule.getInstance().getCoreSystem().getPlayerUtils().getSkinFromSkinDatabase(entityData.getSkinName());
                        break;
                    case PLAYER:
                        skin = NpcModule.getInstance().getCoreSystem().getPlayerUtils().getSkinInfo(entityData.getSkinName());
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

    @Override
    protected void changeDisplayname(String displayname) {
        this.profile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', displayname));
        this.profile.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));

        super.changeDisplayname(displayname);
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
    public void setEquipment(EquipmentPosition position, ItemStack item) {
        entityData.getEquipment().set(position.getId(), item);
        sendPackets(makeEquipmentPacket(position.getId()));
    }

    @Override
    public void setSkin(SkinInfo skin) {
        this.skin = skin;

        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        for (Player p : visiblePlayersList) {
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
    public void setTablistName(String name) {
        if (!entityData.getTablistName().equals(name)) {
            if (entityData.isVisibleOnTab()) {
                sendPackets(makeTablistPacket(false), makeTablistPacket(true));
            }
            entityData.setTablistName(name);
        }
    }

    @Override
    public void setVisibleOnTab(boolean visible) {
        if (entityData.isVisibleOnTab() != visible) {
            sendPackets(makeTablistPacket(visible));
            entityData.setVisibleOnTab(visible);
        }
    }


    private PacketPlayOutPlayerInfo makeTablistPacket(boolean add) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionManager.setValue(packet, "a", add ? PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER : PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                packet.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.CREATIVE, CraftChatMessage.fromString(entityData.getTablistName())[0])
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

}

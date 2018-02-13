/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.listener.PlayerDeath;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickManager {

    private Map<UUID, TextureData> oldProfiles;
    private boolean allowNicking;

    public NickManager(boolean allowNicking) {
        this.oldProfiles = new HashMap<>();
        this.allowNicking = allowNicking;
    }

    public void nick(Player p, String name, String value, String signature) {
        if (allowNicking) {
            CorePlayer cp = CoreSystem.getCorePlayer(p);

            if (!cp.isNicked()) {
                setNick(p, name, new TextureData(value, signature));

                cp.setNickname(name);
                cp.setNicked(true);
                p.setDisplayName(name);

                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Dein Nickname ist nun §f" + name);
            } else {
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du bist bereits genickt!");
            }
        } else {
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du kannst dich während des Spiels nicht nicken!");
        }
    }

    public void setNicks(Player p) {
        for (CorePlayer cp : CoreSystem.getOnlineCorePlayers()) {
            if (cp.isNicked()) {
                Player player = cp.bukkit();
                EntityPlayer ep = ((CraftPlayer) player).getHandle();
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(player != p ? new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()) : new PacketPlayOutRespawn(p.getEntityId(), EnumDifficulty.EASY, ep.world.G(), ep.playerInteractManager.getGameMode()));

                CoreSystem.getCorePlayer(p).getScoreboard().reload();
            }
        }
    }

    public void unnick(Player p) {
        if (allowNicking) {
            CorePlayer cp = CoreSystem.getCorePlayer(p);

            if (cp.isNicked()) {
                setNick(p, cp.getName(), oldProfiles.get(p.getUniqueId()));

                cp.setNickname(null);
                cp.setNicked(false);
                p.setDisplayName(cp.getName());
                oldProfiles.remove(p.getUniqueId());

                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "Du bist nun nicht mehr genickt!");
            } else {
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du bist nicht genickt!");
            }
        } else {
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du kannst dich während des Spiels nicht entnicken!");
        }
    }

    private void setNick(Player p, String name, TextureData data) {
        if (data == null) return;
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        GameProfile gp = ((CraftPlayer) p).getProfile();
        for (Property pr : gp.getProperties().values()) {
            if (pr.getName().equalsIgnoreCase("textures"))
                oldProfiles.put(p.getUniqueId(), new TextureData(pr.getValue(), pr.getSignature()));
        }
        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", data.getValue(), data.getSignature()));
        try {
            final Field nameField = gp.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            int modifiers = nameField.getModifiers();
            final Field modifierField = nameField.getClass().getDeclaredField("modifiers");
            modifiers &= 0xFFFFFFEF;
            modifierField.setAccessible(true);
            modifierField.setInt(nameField, modifiers);
            nameField.set(gp, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(p.getEntityId()));
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));

            if (!player.equals(p)) {
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()));
            } else {
                PlayerDeath.nicking.add(p);
                p.setHealth(0);
                p.spigot().respawn();
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
            }

            CoreSystem.getCorePlayer(player).getScoreboard().reload();
        }

        p.setDisplayName(name);
        CoreSystem.getCorePlayer(p).getScoreboard().reload();
    }

    private class TextureData {
        private String value;
        private String signature;

        TextureData(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }

        String getValue() {
            return value;
        }
        String getSignature() {
            return signature;
        }
    }

    private class ProfileData {
        private final double health;
        private final int foodlevel;

        ProfileData(Player p) {
            this.health = p.getHealth();
            this.foodlevel = p.getFoodLevel();

        }
    }

    public void setAllowNicking(boolean allowNicking) {
        this.allowNicking = allowNicking;
    }

}

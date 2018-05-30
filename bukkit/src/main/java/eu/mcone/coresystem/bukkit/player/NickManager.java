/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.listener.PlayerDeath;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickManager implements eu.mcone.coresystem.api.bukkit.player.NickManager {

    private BukkitCoreSystem instance;
    private Map<UUID, SkinInfo> oldProfiles;
    @Getter @Setter
    private boolean allowNicking = true;

    public NickManager(BukkitCoreSystem instance) {
        this.instance = instance;
        this.oldProfiles = new HashMap<>();
    }

    @Override
    public void nick(Player p, String name, String value, String signature) {
        if (allowNicking) {
            eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer cp = instance.getCorePlayer(p);

            if (!cp.isNicked()) {
                setNick(p, name, instance.getPlayerUtils().constructSkinInfo(name, value, signature));

                ((BukkitCorePlayer) cp).setNickname(name);
                ((BukkitCorePlayer) cp).setNicked(true);
                p.setDisplayName(name);

                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Dein Nickname ist nun §f" + name);
            } else {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du bist bereits genickt!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du kannst dich während des Spiels nicht nicken!");
        }
    }

    public void setNicks(Player p) {
        for (eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer cp : instance.getOnlineCorePlayers()) {
            if (cp.isNicked()) {
                Player player = cp.bukkit();
                EntityPlayer ep = ((CraftPlayer) player).getHandle();
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(player != p ? new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()) : new PacketPlayOutRespawn(p.getEntityId(), EnumDifficulty.EASY, ep.world.G(), ep.playerInteractManager.getGameMode()));

                instance.getCorePlayer(p).getScoreboard().reload(BukkitCoreSystem.getInstance());
            }
        }
    }

    @Override
    public void unnick(Player p) {
        if (allowNicking) {
            eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer cp = instance.getCorePlayer(p);

            if (cp.isNicked()) {
                setNick(p, cp.getName(), oldProfiles.get(p.getUniqueId()));

                ((BukkitCorePlayer) cp).setNickname(null);
                ((BukkitCorePlayer) cp).setNicked(false);
                p.setDisplayName(cp.getName());
                oldProfiles.remove(p.getUniqueId());

                BukkitCoreSystem.getInstance().getMessager().send(p, "Du bist nun nicht mehr genickt!");
            } else {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du bist nicht genickt!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du kannst dich während des Spiels nicht entnicken!");
        }
    }

    private void setNick(Player p, String name, eu.mcone.coresystem.api.core.player.SkinInfo skin) {
        if (skin == null) return;
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        GameProfile gp = ((CraftPlayer) p).getProfile();
        for (Property pr : gp.getProperties().values()) {
            if (pr.getName().equalsIgnoreCase("textures"))
                oldProfiles.put(p.getUniqueId(), instance.getPlayerUtils().constructSkinInfo(name, pr.getValue(), pr.getSignature()));
        }
        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
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
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));

            if (!player.equals(p)) {
                connection.sendPacket(new PacketPlayOutEntityDestroy(p.getEntityId()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()));

                instance.getCorePlayer(player).getScoreboard().reload(BukkitCoreSystem.getInstance());
            } else {
                PlayerDeath.nicking.add(p);
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
            }
        }

        p.setDisplayName(name);
        instance.getCorePlayer(p).getScoreboard().reload(BukkitCoreSystem.getInstance());
    }

}

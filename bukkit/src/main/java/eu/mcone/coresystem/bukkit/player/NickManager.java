/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class NickManager implements eu.mcone.coresystem.api.bukkit.player.NickManager {

    private final BukkitCoreSystem instance;
    private final Map<UUID, SkinInfo> oldProfiles;
    @Getter
    @Setter
    private boolean allowSkinChange = true;

    public NickManager(BukkitCoreSystem instance) {
        this.instance = instance;
        this.oldProfiles = new HashMap<>();
    }

    @Override
    public void nick(Player p, Nick nick) {
        CorePlayer cp = instance.getCorePlayer(p);

        if (!cp.isNicked()) {
            ((BukkitCorePlayer) cp).setNick(nick);
            ((BukkitCorePlayer) cp).setNicked(true);

            setNick(p, nick.getName(), nick.getSkinInfo());
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Dein Nickname ist nun §a" + nick.getName());
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du bist bereits genickt!");
        }
    }

    public void setNicks(Player p) {
        for (CorePlayer cp : instance.getOnlineCorePlayers()) {
            if (cp.isNicked()) {
                Player player = cp.bukkit();
                EntityPlayer ep = ((CraftPlayer) player).getHandle();
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
                connection.sendPacket(player != p ? new PacketPlayOutNamedEntitySpawn(((CraftPlayer) p).getHandle()) : new PacketPlayOutRespawn(p.getEntityId(), EnumDifficulty.EASY, ep.world.G(), ep.playerInteractManager.getGameMode()));

                instance.getCorePlayer(p).getScoreboard().reload();
            }
        }
    }

    @Override
    public void unnick(Player p, boolean bypassSkin) {
        CorePlayer cp = instance.getCorePlayer(p);

        if (cp.isNicked()) {
            ((BukkitCorePlayer) cp).setNick(null);
            ((BukkitCorePlayer) cp).setNicked(false);

            if (!bypassSkin) {
                setNick(p, cp.getName(), oldProfiles.get(p.getUniqueId()));
                oldProfiles.remove(p.getUniqueId());
            } else {
                setNick(p, cp.getName(), ((CraftPlayer) p).getProfile());
            }
            BukkitCoreSystem.getInstance().getMessenger().send(p, "Du bist nun nicht mehr genickt!");
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du bist nicht genickt!");
        }
    }

    @SuppressWarnings("deprecation")
    private void setNick(Player p, String name, eu.mcone.coresystem.api.core.player.SkinInfo skin) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        GameProfile gp = ((CraftPlayer) p).getProfile();
        for (Property pr : gp.getProperties().values()) {
            if (pr.getName().equalsIgnoreCase("textures"))
                oldProfiles.put(p.getUniqueId(), instance.getPlayerUtils().constructSkinInfo(name, pr.getValue(), pr.getSignature()));
        }
        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        ep.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));

        Bukkit.getScheduler().runTaskLater(CoreSystem.getInstance(), () -> {
            boolean flying = p.isFlying();
            Location location = p.getLocation();
            int level = p.getLevel();
            float xp = p.getExp();
            double maxHealth = p.getMaxHealth();
            double health = p.getHealth();

            ep.playerConnection.sendPacket(new PacketPlayOutRespawn(0, ((CraftWorld) p.getWorld()).getHandle().getDifficulty(), ((CraftWorld) p.getWorld()).getHandle().worldData.getType(), WorldSettings.EnumGamemode.getById(p.getGameMode().getValue())));

            p.setFlying(flying);
            p.teleport(location);
            p.updateInventory();
            p.setLevel(level);
            p.setExp(xp);
            p.setMaxHealth(maxHealth);
            p.setHealth(health);

            ep.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
        }, 1);

        setNick(p, name, gp);
    }

    private void setNick(Player p, String name, GameProfile gameProfile) {
        setGameProfileName(gameProfile, name);

        Bukkit.getScheduler().runTask(CoreSystem.getInstance(), () -> {
            List<Player> canSee = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.canSee(p)) {
                    canSee.add(player);
                    player.hidePlayer(p);
                }
            }
            for (Player player : canSee) {
                player.showPlayer(p);
                instance.getCorePlayer(player).getScoreboard().reload();
            }
        });

        if (instance.getCorePlayer(p).getScoreboard() != null) {
            instance.getCorePlayer(p).getScoreboard().reload();
        }
    }

    public static void setGameProfileName(GameProfile gp, String name) {
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
    }

}

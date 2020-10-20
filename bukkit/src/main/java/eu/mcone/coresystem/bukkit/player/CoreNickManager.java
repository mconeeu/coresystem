/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class CoreNickManager implements eu.mcone.coresystem.api.bukkit.player.NickManager {

    private final BukkitCoreSystem instance;
    private final Map<UUID, SkinInfo> oldProfiles;
    @Getter
    @Setter
    private boolean allowSkinChange = true;

    public CoreNickManager(BukkitCoreSystem instance) {
        this.instance = instance;
        this.oldProfiles = new HashMap<>();
    }

    @Override
    public void nick(Player p, Nick nick, boolean notify) {
        CorePlayer cp = instance.getCorePlayer(p);

        if (!cp.isNicked()) {
            ((BukkitCorePlayer) cp).setNick(nick);
            ((BukkitCorePlayer) cp).setNicked(true);

            if (allowSkinChange) {
                setNick(p, nick.getName(), nick.getUuid(), nick.getSkinInfo());
            } else {
                setNick(p, ((CraftPlayer) p).getHandle().getProfile(), nick.getName(), nick.getUuid());
            }

            if (notify) {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Dein Nickname ist nun §a" + nick.getName());
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du bist bereits genickt!");
        }
    }

    @Override
    public void unnick(Player p) {
        CorePlayer cp = instance.getCorePlayer(p);

        if (cp.isNicked()) {
            ((BukkitCorePlayer) cp).setNick(null);
            ((BukkitCorePlayer) cp).setNicked(false);

            if (allowSkinChange) {
                setNick(p, cp.getName(), cp.getUuid(), oldProfiles.get(p.getUniqueId()));
                oldProfiles.remove(p.getUniqueId());
            } else {
                setNick(p, ((CraftPlayer) p).getProfile(), cp.getName(), cp.getUuid());
            }
            BukkitCoreSystem.getInstance().getMessenger().send(p, "Du bist nun nicht mehr genickt!");
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du bist nicht genickt!");
        }
    }

    private void setNick(Player p, String name, UUID uuid, eu.mcone.coresystem.api.core.player.SkinInfo skin) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        GameProfile gp = ((CraftPlayer) p).getProfile(), privateGp = new GameProfile(p.getUniqueId(), name);
        for (Property property : gp.getProperties().values()) {
            if (property.getName().equalsIgnoreCase("textures"))
                oldProfiles.put(p.getUniqueId(), instance.getPlayerUtils().constructSkinInfo(name, property.getValue(), property.getSignature()));
        }
        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        privateGp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        setNick(p, gp, name, uuid);

        boolean flying = p.isFlying();
        Location location = p.getLocation();
        int level = p.getLevel();
        float xp = p.getExp();
        double maxHealth = p.getMaxHealth();
        double health = p.getHealth();

        ep.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionManager.setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                packet.new PlayerInfoData(privateGp, ep.ping, ep.playerInteractManager.getGameMode(), ep.getPlayerListName())
        )));
        ep.playerConnection.sendPacket(packet);

        ep.playerConnection.sendPacket(new PacketPlayOutRespawn(
                p.getWorld().getEnvironment().getId(),
                ((CraftWorld) p.getWorld()).getHandle().getDifficulty(),
                ((CraftWorld) p.getWorld()).getHandle().worldData.getType(),
                WorldSettings.EnumGamemode.getById(p.getGameMode().getValue())
        ));

        p.setFlying(flying);
        p.teleport(location);
        p.updateInventory();
        p.setLevel(level);
        p.setExp(xp);
        p.setMaxHealth(maxHealth);
        p.setHealth(health);
    }

    public void setNick(Player p, GameProfile gp, String name, UUID uuid) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        List<Player> canSee = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != p && player.canSee(p)) {
                canSee.add(player);

                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
                ReflectionManager.setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
                ReflectionManager.setValue(packet, "b", new ArrayList<>(Collections.singleton(
                        packet.new PlayerInfoData(new GameProfile(gp.getId(), gp.getName()), ep.ping, ep.playerInteractManager.getGameMode(), ep.getPlayerListName())
                )));
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        setGameProfileName(gp, name, uuid);

        for (Player player : canSee) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep)
            );
            instance.getCorePlayer(player).getScoreboard().reload();
        }

        if (instance.getCorePlayer(p).getScoreboard() != null) {
            instance.getCorePlayer(p).getScoreboard().reload();
        }
    }

    public static void setGameProfileName(GameProfile gp, String name, UUID uuid) {
        try {
            final Field nameField = gp.getClass().getDeclaredField("name"), uuidField = gp.getClass().getDeclaredField("id");
            nameField.setAccessible(true);
            uuidField.setAccessible(true);

            int modifiers = nameField.getModifiers();
            final Field modifierField = Field.class.getDeclaredField("modifiers");
            modifiers &= 0xFFFFFFEF;
            modifierField.setAccessible(true);
            modifierField.setInt(nameField, modifiers);

            nameField.set(gp, name);
            uuidField.set(gp, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable() {
        for (CorePlayer cp : BukkitCoreSystem.getSystem().getOnlineCorePlayers()) {
            if (cp.isNicked()) {
                Player p = cp.bukkit();
                GameProfile gp = ((CraftPlayer) p).getProfile();
                SkinInfo skin = oldProfiles.getOrDefault(p.getUniqueId(), null);

                if (skin != null) {
                    gp.getProperties().removeAll("textures");
                    gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
                }

                setGameProfileName(gp, cp.getName(), cp.getUuid());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public int getOtherDimension(World.Environment environment) {
        switch (environment) {
            case THE_END:
                return World.Environment.NORMAL.getId();
            case NORMAL:
                return World.Environment.NETHER.getId();
            case NETHER:
            default:
                return World.Environment.THE_END.getId();
        }
    }

}

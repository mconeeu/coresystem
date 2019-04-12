/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.player.NickManager;
import eu.mcone.coresystem.bukkit.player.PermissibleBase;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public class CorePlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        setPermissions(p);

        Property textures = ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next();
        new BukkitCorePlayer(BukkitCoreSystem.getInstance(), e.getAddress(), new SkinInfo(p.getName(), textures.getValue(), textures.getSignature(), SkinInfo.SkinType.PLAYER), p);
        p.setDisplayName(p.getName());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player bp = e.getPlayer();
        BukkitCorePlayer p = (BukkitCorePlayer) BukkitCoreSystem.getSystem().getCorePlayer(bp);

        e.setJoinMessage(null);
        p.setScoreboard(new MainScoreboard());
        p.registerPacketListener(bp);
        ((NickManager) BukkitCoreSystem.getInstance().getNickManager()).setNicks(bp);

        for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
            cp.getScoreboard().reload();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuitMonitor(PlayerQuitEvent e){
        BukkitCorePlayer p = (BukkitCorePlayer) BukkitCoreSystem.getSystem().getCorePlayer(e.getPlayer());
        p.unregisterAttachment();

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getInstance(), p::unregister);
    }

    public static void setPermissions(Player p) {
        Field f;

        try {
            f = CraftHumanEntity.class.getDeclaredField("perm");
            f.setAccessible(true);
            f.set(p, new PermissibleBase(p));
            f.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }

}
/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.player.CorePermissibleBase;
import eu.mcone.coresystem.bukkit.player.NickManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CorePlayerListener implements Listener {

    public final static CoreActionBar LOADING_MSG = CoreSystem.getInstance().createActionBar().message("§7§oDeine Daten werden geladen...");
    public final static CoreActionBar LOADING_SUCCESS_MSG = CoreSystem.getInstance().createActionBar().message("§2§oDeine Daten wurden geladen!");

    public static final Map<UUID, BukkitTask> teleports = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        setCorePermissibleBase(p);

        Property textures = ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next();
        new BukkitCorePlayer(
                BukkitCoreSystem.getInstance(),
                e.getAddress(),
                new SkinInfo(p.getName(), textures.getValue(), textures.getSignature(), SkinInfo.SkinType.PLAYER),
                p
        );

        p.setDisplayName(p.getName());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        loadCorePlayer(e.getPlayer(), CorePlayerLoadedEvent.Reason.JOIN);
    }

    public void loadCorePlayer(Player p, CorePlayerLoadedEvent.Reason loadReason) {
        LOADING_MSG.send(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));

        if (loadReason.equals(CorePlayerLoadedEvent.Reason.JOIN)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != p) {
                    player.hidePlayer(p);
                    p.hidePlayer(player);
                }
            }
        }

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
            CorePlayerLoadedEvent e = new CorePlayerLoadedEvent(loadReason, BukkitCoreSystem.getInstance().getCorePlayer(p), p);
            Bukkit.getPluginManager().callEvent(e);

            if (!e.isHidden()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player != p) {
                        player.showPlayer(p);
                        p.showPlayer(player);
                    }
                }
            }

            LOADING_SUCCESS_MSG.send(p);
            p.getActivePotionEffects().clear();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCorePlayerLoaded(CorePlayerLoadedEvent e) {
        BukkitCorePlayer p = (BukkitCorePlayer) e.getPlayer();
        Player bp = e.getBukkitPlayer();

        p.setScoreboard(new MainScoreboard());
        p.registerPacketListener(bp);
        ((NickManager) BukkitCoreSystem.getInstance().getNickManager()).setNicks(bp);

        for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
            cp.getScoreboard().reload();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (teleports.containsKey(p.getUniqueId()) && (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
            teleports.get(p.getUniqueId()).cancel();
            teleports.remove(p.getUniqueId());

            BukkitCoreSystem.getSystem().getMessager().send(p, "§4Der Teleportvorgang wurde abgebrochen, weil du dich bewegt hast!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuitMonitor(PlayerQuitEvent e) {
        BukkitCorePlayer p = (BukkitCorePlayer) BukkitCoreSystem.getSystem().getCorePlayer(e.getPlayer());
        p.unregisterAttachment();

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getInstance(), p::unregister);
    }

    public static void setCorePermissibleBase(Player p) {
        Field f;

        try {
            f = CraftHumanEntity.class.getDeclaredField("perm");
            f.setAccessible(true);
            f.set(p, new CorePermissibleBase(p));
            f.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }

}
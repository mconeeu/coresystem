/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.command.BuildCMD;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuildSystem implements Listener {

    @Getter
    private static BuildSystem instance;
    private Set<UUID> allowedPlayers;
    private boolean notify;

    public enum BuildEvent {
        BLOCK_BREAK(new Listener() {
            @EventHandler
            public void on(BlockBreakEvent e) {
                Player p = e.getPlayer();

                if (getInstance().isNotAllowedBuild(p)) {
                    e.setCancelled(true);
                    if (getInstance().notify) p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du darfst hier nicht abbauen!");
                }
            }

            @EventHandler
            public void on(HangingBreakByEntityEvent e) {
                if (e.getRemover() instanceof Player) {
                    Player p = (Player) e.getRemover();

                    if (getInstance().isNotAllowedBuild(p)) {
                        e.setCancelled(true);
                    }
                }
            }

            @EventHandler
            public void on(PlayerInteractEvent e) {
                Player p = e.getPlayer();

                if (getInstance().isNotAllowedBuild(p)) {
                    if ((e.getAction() == Action.PHYSICAL)) {
                        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOIL) {
                            e.setCancelled(true);
                            if (getInstance().notify) p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du darfst das Feld nicht zertrampeln!");
                        }
                    }
                }
            }
        }),
        BLOCK_PLACE(new Listener() {
            @EventHandler
            public void on(BlockPlaceEvent e) {
                Player p = e.getPlayer();

                if (getInstance().isNotAllowedBuild(p)) {
                    e.setCancelled(true);
                    if (getInstance().notify) p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du darfst hier keine Blöcke bauen!");
                }
            }
        }),
        INTERACT(new Listener() {
            @EventHandler
            public void on(PlayerInteractEvent e) {
                Player p = e.getPlayer();

                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    if (getInstance().notify) p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du darfst mit diesem Block nicht interagieren!");
                    e.setCancelled(true);
                }
            }
        });

        @Getter
        private Listener listener;

        BuildEvent(Listener listener) {
            this.listener = listener;
        }

    }

    public BuildSystem(boolean notify, BuildEvent... events) {
        instance = this;
        this.allowedPlayers = new HashSet<>();
        this.notify = notify;

        for (BuildEvent event : events) {
            CoreSystem.getInstance().getServer().getPluginManager().registerEvents(event.getListener(), CoreSystem.getInstance());
        }

        CoreSystem.getInstance().getCommand("build").setExecutor(new BuildCMD(this));
    }

    public void changeBuildMode(Player p) {
        if (allowedPlayers.contains(p.getUniqueId())) {
            allowedPlayers.remove(p.getUniqueId());
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du kannst nun nicht mehr bauen!");
        } else {
            allowedPlayers.add(p.getUniqueId());
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du kannst nun bauen!");
        }
    }

    private boolean isNotAllowedBuild(Player p) {
        return !allowedPlayers.contains(p.getUniqueId());
    }

    public boolean hasBuildModeEnabled(Player p) {
        return allowedPlayers.contains(p.getUniqueId());
    }

}

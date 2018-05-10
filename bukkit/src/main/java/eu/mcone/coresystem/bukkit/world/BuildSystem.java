/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.BuildCMD;
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

public class BuildSystem implements Listener, eu.mcone.coresystem.api.bukkit.world.BuildSystem {

    private static BuildSystem system;
    private Set<UUID> allowedPlayers;
    private boolean notify;

    public BuildSystem(BukkitCoreSystem instance, boolean notify, BuildEvent... events) {
        system = this;
        this.allowedPlayers = new HashSet<>();
        this.notify = notify;

        instance.getCommand("build").setExecutor(new BuildCMD(this));
        for (BuildEvent event : events) {
            switch (event) {
                case BLOCK_BREAK: {
                    instance.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void on(BlockBreakEvent e) {
                            Player p = e.getPlayer();

                            if (system.isNotAllowedBuild(p)) {
                                e.setCancelled(true);
                                if (system.notify)
                                    Messager.send(p, "§4Du darfst hier nicht abbauen!");
                            }
                        }

                        @EventHandler
                        public void on(HangingBreakByEntityEvent e) {
                            if (e.getRemover() instanceof Player) {
                                Player p = (Player) e.getRemover();

                                if (system.isNotAllowedBuild(p)) {
                                    e.setCancelled(true);
                                }
                            }
                        }

                        @EventHandler
                        public void on(PlayerInteractEvent e) {
                            Player p = e.getPlayer();

                            if (system.isNotAllowedBuild(p)) {
                                if ((e.getAction() == Action.PHYSICAL)) {
                                    if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOIL) {
                                        e.setCancelled(true);
                                        if (system.notify)
                                            Messager.send(p, "§4Du darfst das Feld nicht zertrampeln!");
                                    }
                                }
                            }
                        }
                    }, instance);
                    break;
                }
                case BLOCK_PLACE: {
                    instance.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void on(BlockPlaceEvent e) {
                            Player p = e.getPlayer();

                            if (system.isNotAllowedBuild(p)) {
                                e.setCancelled(true);
                                if (system.notify)
                                    Messager.send(p, "§4Du darfst hier keine Blöcke bauen!");
                            }
                        }
                    }, instance);
                    break;
                }
                case INTERACT: {
                    instance.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void on(PlayerInteractEvent e) {
                            Player p = e.getPlayer();

                            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                                if (system.notify) Messager.send(p, "§4Du darfst mit diesem Block nicht interagieren!");
                                e.setCancelled(true);
                            }
                        }

                    }, instance);
                    break;
                }
            }
        }
    }

    public void changeBuildMode(Player p) {
        if (allowedPlayers.contains(p.getUniqueId())) {
            allowedPlayers.remove(p.getUniqueId());
            Messager.send(p, "§4Du kannst nun nicht mehr bauen!");
        } else {
            allowedPlayers.add(p.getUniqueId());
            p.setGameMode(GameMode.CREATIVE);
            Messager.send(p, "§2Du kannst nun bauen!");
        }
    }

    private boolean isNotAllowedBuild(Player p) {
        return !allowedPlayers.contains(p.getUniqueId());
    }

    public boolean hasBuildModeEnabled(Player p) {
        return allowedPlayers.contains(p.getUniqueId());
    }

}

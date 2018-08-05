/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.BuildCMD;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class BuildSystem implements Listener, eu.mcone.coresystem.api.bukkit.world.BuildSystem {

    private Map<UUID, GameMode> allowedPlayers;
    private Map<BuildEvent, Set<Material>> filteredBlocks;

    private boolean notify = false;

    public BuildSystem(BukkitCoreSystem instance, BuildEvent... events) {
        this.allowedPlayers = new HashMap<>();
        this.filteredBlocks = new HashMap<>();

        instance.getPluginManager().registerCoreCommand(new BuildCMD(this), CoreSystem.getInstance());

        for (BuildEvent event : events) {
            switch (event) {
                case BLOCK_BREAK: {
                    instance.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void on(BlockBreakEvent e) {
                            Player p = e.getPlayer();

                            if (isNotAllowedBuild(p) && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new HashSet<>()).contains(e.getBlock().getType())) {
                                e.setCancelled(true);
                                if (notify)
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du darfst hier nicht abbauen!");
                            }
                        }

                        @EventHandler
                        public void on(HangingBreakByEntityEvent e) {
                            if (e.getRemover() instanceof Player) {
                                Player p = (Player) e.getRemover();

                                if (isNotAllowedBuild(p) && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new HashSet<>()).contains(Material.ITEM_FRAME)) {
                                    e.setCancelled(true);
                                }
                            }
                        }

                        @EventHandler
                        public void on(PlayerInteractEvent e) {
                            Player p = e.getPlayer();

                            if (isNotAllowedBuild(p) && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new HashSet<>()).contains(Material.SOIL)) {
                                if ((e.getAction() == Action.PHYSICAL)) {
                                    if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOIL) {
                                        e.setCancelled(true);
                                        if (notify)
                                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du darfst das Feld nicht zertrampeln!");
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

                            if (isNotAllowedBuild(p) && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_PLACE, new HashSet<>()).contains(e.getBlock().getType())) {
                                e.setCancelled(true);
                                if (notify)
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du darfst hier keine Blöcke bauen!");
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

                            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isNotAllowedBuild(p) && !filteredBlocks.getOrDefault(BuildEvent.INTERACT, new HashSet<>()).contains(e.getClickedBlock().getType())) {
                                if (notify) BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du darfst mit diesem Block nicht interagieren!");
                                e.setCancelled(true);
                            }
                        }

                        @EventHandler
                        public void on(PlayerInteractAtEntityEvent e) {
                            if (e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
                                e.setCancelled(true);
                            }
                        }
                    }, instance);
                    break;
                }
            }
        }
    }

    @Override
    public void setNotifying(boolean notify) {
        this.notify = notify;
    }

    @Override
    public void addFilter(BuildEvent event, Material... filter) {
        if (filteredBlocks.containsKey(event)) {
            filteredBlocks.get(event).addAll(Arrays.asList(filter));
        } else {
            filteredBlocks.put(event, new HashSet<>(Arrays.asList(filter)));
        }
    }

    @Override
    public void changeBuildMode(Player p) {
        if (allowedPlayers.containsKey(p.getUniqueId())) {
            p.setGameMode(allowedPlayers.get(p.getUniqueId()));
            allowedPlayers.remove(p.getUniqueId());
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du kannst nun nicht mehr bauen!");
        } else {
            allowedPlayers.put(p.getUniqueId(), p.getGameMode());
            p.setGameMode(GameMode.CREATIVE);
            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du kannst nun bauen!");
        }
    }

    @Override
    public boolean hasBuildModeEnabled(Player p) {
        return allowedPlayers.containsKey(p.getUniqueId());
    }

    private boolean isNotAllowedBuild(Player p) {
        return !allowedPlayers.containsKey(p.getUniqueId());
    }

}

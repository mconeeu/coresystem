/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.BuildModeChangeEvent;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.BuildCMD;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class BuildSystem implements Listener, eu.mcone.coresystem.api.bukkit.world.BuildSystem {

    private static final Set<EntityType> RELEVANT_ENTITY_TYPES = new HashSet<>(Arrays.asList(
            EntityType.PAINTING,
            EntityType.ITEM_FRAME,
            EntityType.ARMOR_STAND,
            EntityType.MINECART_COMMAND,
            EntityType.BOAT,
            EntityType.MINECART,
            EntityType.MINECART_CHEST,
            EntityType.MINECART_FURNACE,
            EntityType.MINECART_TNT,
            EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER,
            EntityType.ENDER_CRYSTAL,
            EntityType.ARMOR_STAND
    ));

    private Map<UUID, GameMode> allowedPlayers;
    private Map<BuildEvent, List<Integer>> filteredBlocks;

    @Setter
    private boolean notify = false, useBuildPermissionNodes = false;
    private ListMode listMode = ListMode.BLACKLIST;
    private List<World> worlds = new ArrayList<>();

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

                            if (applyRules(p.getWorld())) {
                                if (
                                        (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".break." + e.getBlock().getType().getId()))
                                                && isNotAllowedBuild(p)
                                                && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new ArrayList<>()).contains(e.getBlock().getType().getId())
                                ) {
                                    e.setCancelled(true);
                                    if (notify)
                                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst hier nicht abbauen!");
                                }
                            }
                        }

                        @EventHandler
                        public void on(EntityDamageByEntityEvent e) {
                            if (e.getDamager() instanceof Player) {
                                Player p = (Player) e.getDamager();

                                if (applyRules(p.getWorld())) {
                                    if (RELEVANT_ENTITY_TYPES.contains(e.getEntity().getType())) {
                                        if (
                                                (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".break." + e.getEntity().getType().getTypeId()))
                                                        && isNotAllowedBuild(p)
                                                        && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new ArrayList<>()).contains((int) e.getEntity().getType().getTypeId())
                                        ) {
                                            e.setCancelled(true);
                                            if (notify)
                                                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst hier nicht abbauen!");
                                        }
                                    }
                                }
                            }
                        }

                        @EventHandler
                        public void on(HangingBreakByEntityEvent e) {
                            if (e.getRemover() instanceof Player) {
                                Player p = (Player) e.getRemover();

                                if (applyRules(p.getWorld())) {
                                    if (RELEVANT_ENTITY_TYPES.contains(e.getEntity().getType())) {
                                        if (
                                                (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".break." + e.getEntity().getType().getTypeId()))
                                                        && isNotAllowedBuild(p)
                                                        && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new ArrayList<>()).contains((int) e.getEntity().getType().getTypeId())
                                        ) {
                                            e.setCancelled(true);
                                            if (notify)
                                                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst hier nicht abbauen!");
                                        }
                                    }
                                }
                            }
                        }

                        @EventHandler
                        public void on(PlayerInteractEvent e) {
                            Player p = e.getPlayer();

                            if (applyRules(p.getWorld())) {
                                if ((e.getAction() == Action.PHYSICAL)) {
                                    if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SOIL)) {
                                        if (
                                                (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".break." + Material.SOIL.getId()))
                                                        && isNotAllowedBuild(p)
                                                        && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_BREAK, new ArrayList<>()).contains(Material.SOIL.getId())
                                        ) {
                                            e.setCancelled(true);
                                            if (notify)
                                                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst das Feld nicht zertrampeln!");
                                        }
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

                            if (applyRules(p.getWorld())) {
                                if (
                                        (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".place." + e.getBlock().getType().getId()))
                                                && isNotAllowedBuild(p)
                                                && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_PLACE, new ArrayList<>()).contains(e.getBlock().getType().getId())
                                ) {
                                    e.setCancelled(true);
                                    if (notify)
                                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst hier nicht bauen!");
                                }
                            }
                        }

                        @EventHandler
                        public void on(HangingPlaceEvent e) {
                            Player p = e.getPlayer();

                            if (applyRules(p.getWorld())) {
                                if (
                                        (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".place." + e.getBlock().getType().getId()))
                                                && isNotAllowedBuild(p)
                                                && !filteredBlocks.getOrDefault(BuildEvent.BLOCK_PLACE, new ArrayList<>()).contains(e.getBlock().getType().getId())
                                ) {
                                    e.setCancelled(true);
                                    if (notify)
                                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst hier nicht bauen!");
                                }
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

                            if (applyRules(p.getWorld())) {
                                if (
                                        e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                                                || (e.getAction().equals(Action.PHYSICAL) && !p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SOIL))
                                ) {
                                    if (
                                            (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".interact." + e.getClickedBlock().getType().getId()))
                                                    && isNotAllowedBuild(p)
                                                    && !filteredBlocks.getOrDefault(BuildEvent.INTERACT, new ArrayList<>()).contains(e.getClickedBlock().getType().getId())
                                    ) {
                                        e.setCancelled(true);
                                        if (notify)
                                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst damit nicht interagieren!");
                                    }
                                }
                            }
                        }

                        @EventHandler
                        public void on(PlayerInteractAtEntityEvent e) {
                            Player p = e.getPlayer();

                            if (applyRules(p.getWorld())) {
                                if (RELEVANT_ENTITY_TYPES.contains(e.getRightClicked().getType())) {
                                    if (
                                            (!useBuildPermissionNodes || !p.hasPermission("system.bukkit.build." + p.getWorld().getName().toLowerCase() + ".interact." + e.getRightClicked().getType().getTypeId()))
                                                    && isNotAllowedBuild(p)
                                                    && !filteredBlocks.getOrDefault(BuildEvent.INTERACT, new ArrayList<>()).contains((int) e.getRightClicked().getType().getTypeId())
                                    ) {
                                        e.setCancelled(true);
                                        if (notify)
                                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du darfst damit nicht interagieren!");
                                    }
                                }
                            }
                        }
                    }, instance);
                    break;
                }
            }
        }
    }

    @Override
    public void setWorlds(ListMode mode, World... worlds) {
        this.listMode = mode;
        this.worlds = Arrays.asList(worlds);
    }

    @Override
    public void addFilter(BuildEvent event, Integer... filter) {
        if (filteredBlocks.containsKey(event)) {
            filteredBlocks.get(event).addAll(Arrays.asList(filter));
        } else {
            filteredBlocks.put(event, new ArrayList<>(Arrays.asList(filter)));
        }
    }

    @Override
    public void changeBuildMode(Player p) {
        if (allowedPlayers.containsKey(p.getUniqueId())) {
            p.setGameMode(allowedPlayers.get(p.getUniqueId()));
            allowedPlayers.remove(p.getUniqueId());
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Du kannst nun nicht mehr bauen!");
            Bukkit.getPluginManager().callEvent(new BuildModeChangeEvent(p, false));
        } else {
            allowedPlayers.put(p.getUniqueId(), p.getGameMode());
            p.setGameMode(GameMode.CREATIVE);
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du kannst nun bauen!");
            Bukkit.getPluginManager().callEvent(new BuildModeChangeEvent(p, true));
        }
    }

    @Override
    public boolean hasBuildModeEnabled(Player p) {
        return allowedPlayers.containsKey(p.getUniqueId());
    }

    private boolean isNotAllowedBuild(Player p) {
        return !allowedPlayers.containsKey(p.getUniqueId());
    }

    private boolean applyRules(World w) {
        return (listMode.equals(ListMode.WHITELIST) && worlds.contains(w))
                || (listMode.equals(ListMode.BLACKLIST) && !worlds.contains(w));
    }

}

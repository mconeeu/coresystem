/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.WorldUploader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WorldCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("system.bukkit.world")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§7Im Moment sind folgende Welten geladen:");
                        StringBuilder sb = new StringBuilder();

                        List<World> worlds = Bukkit.getWorlds();
                        for (int i = 0; i < worlds.size(); i++) {
                            sb.append("§3§o").append(worlds.get(i).getName());
                            if (i != worlds.size() - 1) sb.append("§7, ");
                        }

                        p.sendMessage(sb.toString());
                        return true;
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("info")) {
                        World w = Bukkit.getWorld(args[1]);

                        if (w != null) {
                            p.sendMessage("§2Bitte sehr. Ein paar Infos über die Welt §a" + args[1] + "§2:" +
                                    "\n§7Environment: §f" + w.getEnvironment() +
                                    "\n§7WorldType: §f" + w.getWorldType() +
                                    "\n§7Generate Structures: §f" + w.canGenerateStructures() +
                                    "\n§7Difficulty: §f" + w.getDifficulty() +
                                    "\n§7Spawn-Location: §f" + w.getSpawnLocation() +
                                    "\n§7PvP: §f" + w.getPVP() +
                                    "\n§7AutoSave: §f" + w.isAutoSave() +
                                    "\n§7Keep Spawn in Memory: §f" + w.getKeepSpawnInMemory() +
                                    "\n§7Animals allowed: §f" + w.getAllowAnimals() +
                                    "\n§7Monsters allowed: §f" + w.getAllowMonsters()
                            );
                        } else {
                            p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Diese Welt existiert nicht!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("store")) {
                        World w = Bukkit.getWorld(args[1]);

                        if (w != null) {
                            p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§7Die Welt wird hochgeladen...");
                            try {
                                new WorldUploader(w).upload();
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§2Die Welt wurde erfolgreich abgespeichert und kann nun wieder modifiziert werden!");
                            } catch (SQLException | IOException e) {
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Es ist ein Fehler beim speichern der Welt aufgetreten!");
                                e.printStackTrace();
                            }
                        } else {
                            p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Die Welt §c"+args[1]+"§4 existiert nicht!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        World w = Bukkit.getWorld(args[1]);

                        if (w != null) {
                            if (BukkitCoreSystem.getInstance().getWorldManager().removeWorld(w)) {
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§2Die Welt wurde erfolgreich gelöscht!");
                            } else {
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Es ist ein Fehler beim löschen der Welt aufgetreten!");
                            }
                        }
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("import")) {
                        World w = Bukkit.getWorld(args[1]);

                        if (w == null) {
                            World.Environment env = null;
                            for (World.Environment environment : World.Environment.values()) {
                                if (environment.toString().equalsIgnoreCase(args[2])) {
                                    env = environment;
                                }
                            }

                            if (env != null) {
                                BukkitCoreSystem.getInstance().getWorldManager().addWorld(args[1], World.Environment.valueOf(args[2]));
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§2Die Welt " + args[1] + " wurde erfolgreich geladen!");
                            } else {
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Es existieren nur diese Environments: §cNORMAL§4, §cNETHER§4, §cTHE_END");
                            }
                        } else {
                            p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Eine Welt mit dem Namen §c"+args[1]+"§4 ist bereits geladen!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("create")) {
                        World w = Bukkit.getWorld(args[1]);

                        if (w == null) {
                            World.Environment env = null;
                            for (World.Environment environment : World.Environment.values()) {
                                if (environment.toString().equalsIgnoreCase(args[2])) {
                                    env = environment;
                                }
                            }

                            if (env != null) {
                                BukkitCoreSystem.getInstance().getWorldManager().addWorld(args[1], World.Environment.valueOf(args[2]));
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§2Die Welt " + args[1] + " wurde erfolgreich erstellt!");
                            } else {
                                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Es existieren nur diese Environments: §cNORMAL§4, §cNETHER§4, §cTHE_END");
                            }
                        } else {
                            p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Eine Welt mit dem Namen §c"+args[1]+"§4 ist bereits geladen!");
                        }
                    }
                }

                p.sendMessage("§4Bitte benutze: §c/world <list | load> [<name>] [<NORMAL | NETHER | THE_END>]");
            } else {
                p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                return true;
            }
        }
        return false;
    }

}

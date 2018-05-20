/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("system.bukkit.world")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        Messager.send(p, "§7Im Moment sind folgende Welten geladen:");
                        StringBuilder sb = new StringBuilder();

                        List<World> worlds = Bukkit.getWorlds();
                        for (int i = 0; i < worlds.size(); i++) {
                            sb.append("§3§o").append(worlds.get(i).getName());
                            if (i != worlds.size() - 1) sb.append("§7, ");
                        }

                        p.sendMessage(sb.toString());
                        return true;
                    } else if (args[0].equalsIgnoreCase("setspawn")) {
                        if (p.hasPermission("system.bukkit.world.setspawn")) {
                            CoreSystem.getInstance().getCorePlayer(p).getWorld().setSpawnLocation(p.getLocation());
                            Messager.send(p, "§2Der Spawn wurde erfolgreich gesetzt!");
                        } else {
                            Messager.sendTransl(p, "system.command.noperm");
                        }
                    } else if (args[0].equalsIgnoreCase("keys")) {
                        Messager.send(p, "§2Diese Keys können bei §a/world set <key> <value>§2 oder bei §a/world create [<key>=<value>]... §2verwendet werden:" +
                                "\n§7§oname §8: §f§o{name}" +
                                "\n§7§oseed §8: §f§o{seed}" +
                                "\n§7§otype §8: §f{NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, CUSTOMIZED}" +
                                "\n§7§oenvironment §8: §f{NORMAL, NETHER, THE_END}" +
                                "\n§7§odifficulty §8: §f{NORMAL, NETHER, THE_END}" +
                                "\n§7§ogenerator §8: §f§o{name}" +
                                "\n§7§ogeneratorSettings §8: §f§o{settings}" +
                                "\n§7§ogenerateStructures §8: §f{true, false}" +
                                "\n§7§oautoSave §8: §f{true, false}" +
                                "\n§7§opvp §8: §f{true, false}" +
                                "\n§7§oallowAnimals §8: §f{true, false}" +
                                "\n§7§oallowMonsters §8: §f{true, false}" +
                                "\n§7§okeepSpawnInMemory §8: §f{true, false}"
                        );
                    } else if(args[0].equalsIgnoreCase("reload")) {
                        CoreSystem.getInstance().getWorldManager().reload();
                        Messager.send(p, "§2Du hast alle world Configurationen neu geladen!");
                    }
                } else if (args.length >= 2) {
                    if (args.length == 2) {
                        CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                        if (w != null) {
                            if (args[0].equalsIgnoreCase("tp")) {
                                p.teleport(w.bukkit().getSpawnLocation());
                                Messager.send(p, "§4Du wurdest erfolgreich zur Welt §a" + w.getName() + "§2 gesendet!");

                                return true;
                            } else if (args[0].equalsIgnoreCase("info")) {
                                World bw = w.bukkit();
                                p.sendMessage("");
                                Messager.send(p, "§2Bitteschön, Ein paar Infos über die Welt §a" + args[1] + "§2:" +
                                        "\n§7§oSpawn-Location: §f" + bw.getSpawnLocation() +
                                        "\n§7§oSeed: §f" + bw.getSeed() +
                                        "\n§7§oWorldType: §f" + bw.getWorldType() +
                                        "\n§7§oEnvironment: §f" + bw.getEnvironment() +
                                        "\n§7§oDifficulty: §f" + bw.getDifficulty() +
                                        "\n§7§oGenerator: §f" + w.getGenerator() +
                                        "\n§7§oGenerator-Settings: §f" + w.getGeneratorSettings() +
                                        "\n§7§oGenerate Structures: §f" + bw.canGenerateStructures() +
                                        "\n§7§oAutoSave: §f" + bw.isAutoSave() +
                                        "\n§7§oPvP: §f" + bw.getPVP() +
                                        "\n§7§oAnimals allowed: §f" + bw.getAllowAnimals() +
                                        "\n§7§oMonsters allowed: §f" + bw.getAllowMonsters() +
                                        "\n§7§oKeep Spawn in Memory: §f" + bw.getKeepSpawnInMemory()
                                );

                                return true;
                            } else if (args[0].equalsIgnoreCase("upload")) {
                                if (p.hasPermission("system.bukkit.world.upload")) {
                                    Messager.send(p, "§7Die Welt wird hochgeladen...");
                                    if (w.upload()) {
                                        Messager.send(p, "§2Die Welt wurde erfolgreich abgespeichert und kann nun wieder modifiziert werden!");
                                    } else {
                                        Messager.send(p, "§4Es ist ein Fehler beim speichern der Welt aufgetreten!");
                                    }
                                } else {
                                    Messager.sendTransl(p, "system.command.noperm");
                                }
                                return true;
                            } else if (args[0].equalsIgnoreCase("delete")) {
                                if (p.hasPermission("system.bukkit.world.delete")) {
                                    if (w.delete()) {
                                        Messager.send(p, "§2Die Welt wurde erfolgreich gelöscht!");
                                    } else {
                                        Messager.send(p, "§4Es ist ein Fehler beim löschen der Welt aufgetreten!");
                                    }
                                } else {
                                    Messager.sendTransl(p, "system.command.noperm");
                                }
                                return true;
                            }
                        } else {
                            Messager.send(p, "§4Diese Welt existiert nicht!");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("location")) {
                            String locationName = args[2];

                            if (args[1].equalsIgnoreCase("set")) {
                                BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().setLocation(locationName, p.getLocation()).save();
                                Messager.send(p, "§2Die Location wurde erfolgreich abgespeichert");

                                return true;
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                CoreWorld w = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();

                                if (w.getLocations().containsKey(locationName)) {
                                    w.removeLocation(locationName).save();
                                    Messager.send(p, "§2Die Location wurde erfolgreich gelöscht");
                                } else {
                                    Messager.send(p, "§4Die angegebene Location existiert nicht!");
                                }
                                return true;
                            } else if (args[1].equalsIgnoreCase("list")) {
                                StringBuilder sb = new StringBuilder();

                                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(locationName);
                                if (w != null) {
                                    for (HashMap.Entry<String, CoreLocation> loc : w.getLocations().entrySet()) {
                                        sb.append("\n§3§o").append(loc.getKey()).append(" ").append(loc.getValue());
                                    }

                                    Messager.send(p, "§2Hier alle Locations der Welt §a" + locationName + sb.toString());
                                } else {
                                    Messager.send(p, "§4Die angegebene Welt existiert nicht. Bitte benutze §c/world");
                                }

                                return true;
                            }
                        } else if (args[0].equalsIgnoreCase("set")) {
                            if (p.hasPermission("system.bukkit.world.modify")) {
                                CoreWorld w = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();

                                try {
                                    if (args[0].equalsIgnoreCase("name")) {
                                        w.changeName(args[2]);
                                    } else if (args[1].equalsIgnoreCase("type")) {
                                        w.setWorldType(WorldType.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("environment")) {
                                        w.setEnvironment(World.Environment.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("difficulty")) {
                                        w.setDifficulty(Difficulty.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("generator")) {
                                        w.setGenerator(args[2]);
                                    } else if (args[1].equalsIgnoreCase("generatorSettings")) {
                                        w.setGeneratorSettings(args[2]);
                                    } else if (args[1].equalsIgnoreCase("generateStructures")) {
                                        w.generateStructures(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("autoSave")) {
                                        w.getProperties().setAutoSave(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("pvp")) {
                                        w.getProperties().setPvp(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("allowAnimals")) {
                                        w.getProperties().setAllowAnimals(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("allowMonsters")) {
                                        w.getProperties().setAllowMonsters(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("keepSpawnInMemory")) {
                                        w.getProperties().setKeepSpawnInMemory(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("seed")) {
                                        Messager.send(p, "§4Der Seed kann nachträglich nicht verändert werden!");
                                        return true;
                                    } else {
                                        Messager.send(p, "§4Diese Einstellung existiert nicht! Benutze §c/world keys§4 für eine Liste aller Keys!");
                                        return true;
                                    }

                                    w.save();
                                    Messager.send(p, "§2Deine Einstellungen wurden übernommen!");
                                } catch (IllegalArgumentException e) {
                                    Messager.send(p, "§4Diese Einstellung existiert nicht!");
                                }
                            } else {
                                Messager.sendTransl(p, "system.command.noperm");
                            }
                            return true;
                        } else if (args[0].equalsIgnoreCase("import")) {
                            if (p.hasPermission("system.bukkit.world.import")) {
                                World w = Bukkit.getWorld(args[1]);

                                if (w == null) {
                                    if (new File(args[1]).exists()) {
                                        World.Environment env = null;
                                        for (World.Environment environment : World.Environment.values()) {
                                            if (environment.toString().equalsIgnoreCase(args[2])) {
                                                env = environment;
                                            }
                                        }

                                        if (env != null) {
                                            if (BukkitCoreSystem.getInstance().getWorldManager().addWorld(args[1], World.Environment.valueOf(args[2]))) {
                                                Messager.send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich geladen!");
                                            } else {
                                                Messager.send(p, "§4Die Welt §c" + args[1] + "§4 konnte nicht importiert werden! Weitere Infos in der Konsole.");
                                            }
                                        } else {
                                            Messager.send(p, "§4Es existieren nur diese Environments: §cNORMAL§4, §cNETHER§4, §cTHE_END");
                                        }
                                    } else {
                                        Messager.send(p, "§4Ein Weltordner mit dem Namen §c" + args[1] + "§4 existiert nicht!");
                                    }
                                } else {
                                    Messager.send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                                }
                            } else {
                                Messager.sendTransl(p, "system.command.noperm");
                            }
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("create")) {
                        if (p.hasPermission("system.bukkit.world.create")) {
                            World w = Bukkit.getWorld(args[1]);

                            if (w == null) {
                                Map<String, String> settings = new HashMap<>();
                                for (int i = 1; i < args.length; i++) {
                                    String[] setting = args[i].split("=");

                                    if (setting.length < 2) {
                                        Messager.send(p, "§4Bitte benutze: §c/world create <name> [<key>=<value>]...");
                                        return true;
                                    } else {
                                        settings.put(setting[0], setting[1]);
                                    }
                                }

                                try {
                                    if (BukkitCoreSystem.getInstance().getWorldManager().addWorld(args[1], settings)) {
                                        Messager.send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich erstellt!");
                                    } else {
                                        Messager.send(p, "§4Die Welt " + args[1] + " konnte nicht erstellt werden, da wahrscheinlich ein falsches Key-Value Konstrukt verwendet wurde!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    Messager.send(p, "§4Die Welt §c" + args[1] + "§4 wurde erstellt, allerdings konnte mindestens eine Einstellung nicht übernommen werden: \n§7§o" + e.getMessage());
                                }
                            } else {
                                Messager.send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                            }
                        } else {
                            Messager.sendTransl(p, "system.command.noperm");
                        }
                        return true;
                    }
                }

                Messager.send(p, "§4Bitte benutze: " +
                        "\n§c/world list §4oder " +
                        "\n§c/world location <list | set | remove> [<location-name | world-name>]" +
                        "\n§c/world <info | upload | delete | tp> <world-name> §4oder " +
                        "\n§c/world set <key> <value> §4oder " +
                        "\n§c/world import <name> <NORMAL | NETHER | THE_END>" +
                        "\n§c/world create <name> [<key>=<value>]..." +
                        "\n§c/world keys (-> Keys for /world set and /world create)"
                );
            } else {
                Messager.sendTransl(p, "system.command.noperm");
            }
        }

        return true;
    }

}

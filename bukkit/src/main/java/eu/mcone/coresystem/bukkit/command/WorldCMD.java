/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
                if (args.length < 2) {
                    if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§7Du befindest dich gerade auf der Welt: §f"+p.getWorld().getName());
                        ComponentBuilder componentBuilder = new ComponentBuilder("§7Du kannst dich zu folgenden Welten teleportieren: ");

                        List<CoreWorld> worlds = BukkitCoreSystem.getInstance().getWorldManager().getWorlds();
                        for (int i = 0; i < worlds.size(); i++) {
                            CoreWorld world = worlds.get(i);

                            componentBuilder
                                    .append("§3" + world.getName())
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + world.bukkit().getPlayers().size() + " Spieler & NPCs\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world tp " + world.getName()));

                            if (i != worlds.size() - 1) componentBuilder.append(ChatColor.GRAY + ", ");
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                        return true;
                    } else if (args[0].equalsIgnoreCase("setspawn")) {
                        if (p.hasPermission("system.bukkit.world.setspawn")) {
                            CoreSystem.getInstance().getCorePlayer(p).getWorld().setSpawnLocation(p.getLocation());
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Der Spawn wurde erfolgreich gesetzt!");
                        } else {
                            BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("keys")) {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Diese Keys können bei §a/world set <key> <value>§2 oder bei §a/world create [<key>=<value>]... §2verwendet werden:" +
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
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        CoreSystem.getInstance().getWorldManager().reload();
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast alle world Configurationen neu geladen!");
                        return true;
                    } else {
                        for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                            if (args[0].equalsIgnoreCase(w.getName())) {
                                p.performCommand("world tp "+w.getName());
                                return true;
                            }
                        }
                    }
                } else {
                    if (args.length == 2) {
                        CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                        if (w != null) {
                            if (args[0].equalsIgnoreCase("tp")) {
                                p.teleport(w.bukkit().getSpawnLocation());
                                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest zur Welt §a" + w.getName() + "§2 teleportiert!");

                                return true;
                            } else if (args[0].equalsIgnoreCase("info")) {
                                World bw = w.bukkit();
                                p.sendMessage("");
                                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Bitteschön, Ein paar Infos über die Welt §a" + args[1] + "§2:" +
                                        "\n§7§ospawn-location: §f" + bw.getSpawnLocation() +
                                        "\n§7§oseed: §f" + bw.getSeed() +
                                        "\n§7§otype: §f" + bw.getWorldType() +
                                        "\n§7§oenvironment: §f" + bw.getEnvironment() +
                                        "\n§7§odifficulty: §f" + bw.getDifficulty() +
                                        "\n§7§ogenerator: §f" + w.getGenerator() +
                                        "\n§7§ogeneratorSettings: §f" + w.getGeneratorSettings() +
                                        "\n§7§ogenerateStructures: §f" + bw.canGenerateStructures() +
                                        "\n§7§oautoSave: §f" + bw.isAutoSave() +
                                        "\n§7§opvp: §f" + bw.getPVP() +
                                        "\n§7§oallowAnimals: §f" + w.getProperties().isAllowAnimals() +
                                        "\n§7§oallowMonsters: §f" + w.getProperties().isAllowMonsters() +
                                        "\n§7§okeepSpawnInMemory: §f" + bw.getKeepSpawnInMemory()
                                );

                                return true;
                            } else if (args[0].equalsIgnoreCase("upload")) {
                                if (p.hasPermission("system.bukkit.world.upload")) {
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§7Die Welt wird hochgeladen...");
                                    BukkitCoreSystem.getInstance().sendConsoleMessage("Uploading world "+w.getName()+" to database. Initialized by "+p.getName());
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (player.hasPermission("group.builder") && player != p) BukkitCoreSystem.getInstance().getMessager().send(player, "§3"+p.getName()+"§7 lädt gerade die Welt §f"+w.getName()+"§7 in die Datenbank hoch...");
                                    }

                                    if (w.upload()) {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Welt wurde erfolgreich abgespeichert und kann nun wieder modifiziert werden!");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Es ist ein Fehler beim speichern der Welt aufgetreten!");
                                    }
                                } else {
                                    BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                                }
                                return true;
                            } else if (args[0].equalsIgnoreCase("unload")) {
                                if (p.hasPermission("system.bukkit.world.unload")) {
                                    w.unload(true);
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Welt wurde erfolgreich entladen! Benutze §a/world import "+w.getName()+" <environment>§2 um sie wieder zu laden!");
                                } else {
                                    BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                                }
                            } else if (args[0].equalsIgnoreCase("delete")) {
                                if (p.hasPermission("system.bukkit.world.delete")) {
                                    if (w.delete()) {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Welt wurde erfolgreich gelöscht!");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Es ist ein Fehler beim löschen der Welt aufgetreten!");
                                    }
                                } else {
                                    BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                                }
                                return true;
                            }
                        } else {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Diese Welt existiert nicht!");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("location")) {
                            if (p.hasPermission("system.bukkit.world.location")) {
                                String locationName = args[2];

                                if (args[1].equalsIgnoreCase("set")) {
                                    BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().setLocation(locationName, p.getLocation()).save();
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich abgespeichert");

                                    return true;
                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    CoreWorld w = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();

                                    if (w.getLocations().containsKey(locationName)) {
                                        w.removeLocation(locationName).save();
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich gelöscht");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Location existiert nicht!");
                                    }
                                    return true;
                                } else if (args[1].equalsIgnoreCase("list")) {
                                    StringBuilder sb = new StringBuilder();

                                    CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(locationName);
                                    if (w != null) {
                                        if (w.getLocations().size() > 0) {
                                            for (HashMap.Entry<String, CoreLocation> loc : w.getLocations().entrySet()) {
                                                sb.append("\n§3§o").append(loc.getKey()).append(" ").append(loc.getValue());
                                            }

                                            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hier alle Locations der Welt §a" + locationName + sb.toString() + "§2:");
                                        } else {
                                            BukkitCoreSystem.getInstance().getMessager().send(p, "§7Die Welt hat keine abgespeicherten Locations!");
                                        }
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht. Bitte benutze §c/world");
                                    }

                                    return true;
                                } else if (args[1].equalsIgnoreCase("tp") || args[1].equalsIgnoreCase("teleport")) {
                                    CoreLocation loc = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().getLocation(locationName);

                                    if (loc != null) {
                                        p.teleport(loc.bukkit());
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der Location §a" + locationName + "§2 teleportiert!");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Location §c" + locationName + "§4 existiert nicht in dieser Welt!");
                                    }
                                    return true;
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                                return true;
                            }
                        } else if (args[0].equalsIgnoreCase("set")) {
                            if (p.hasPermission("system.bukkit.world.modify")) {
                                BukkitCoreWorld w = (BukkitCoreWorld) BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();

                                try {
                                    if (args[1].equalsIgnoreCase("name")) {
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
                                        if (!Boolean.valueOf(args[2])) w.purgeAnimals();
                                    } else if (args[1].equalsIgnoreCase("allowMonsters")) {
                                        w.getProperties().setAllowMonsters(Boolean.valueOf(args[2]));
                                        if (!Boolean.valueOf(args[2])) w.purgeMonsters();
                                    } else if (args[1].equalsIgnoreCase("keepSpawnInMemory")) {
                                        w.getProperties().setKeepSpawnInMemory(Boolean.valueOf(args[2]));
                                    } else if (args[1].equalsIgnoreCase("seed")) {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Seed kann nachträglich nicht verändert werden!");
                                        return true;
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Diese Einstellung existiert nicht! Benutze §c/world keys§4 für eine Liste aller Keys!");
                                        return true;
                                    }

                                    w.save();
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Deine Einstellungen wurden übernommen!");
                                } catch (IllegalArgumentException e) {
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Diese Einstellung existiert nicht!");
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
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
                                                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich geladen!");
                                            } else {
                                                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Welt §c" + args[1] + "§4 konnte nicht importiert werden! Weitere Infos in der Konsole.");
                                            }
                                        } else {
                                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Es existieren nur diese Environments: §cNORMAL§4, §cNETHER§4, §cTHE_END");
                                        }
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Ein Weltordner mit dem Namen §c" + args[1] + "§4 existiert nicht!");
                                    }
                                } else {
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
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
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/world create <name> [<key>=<value>]...");
                                        return true;
                                    } else {
                                        settings.put(setting[0], setting[1]);
                                    }
                                }

                                try {
                                    if (BukkitCoreSystem.getInstance().getWorldManager().addWorld(args[1], settings)) {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich erstellt!");
                                    } else {
                                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Welt " + args[1] + " konnte nicht erstellt werden, da wahrscheinlich ein falsches Key-Value Konstrukt verwendet wurde!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Welt §c" + args[1] + "§4 wurde erstellt, allerdings konnte mindestens eine Einstellung nicht übernommen werden: \n§7§o" + e.getMessage());
                                }
                            } else {
                                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                            }
                        } else {
                            BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                        }
                        return true;
                    }
                }

                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: " +
                        "\n§c/world <name> " +
                        "\n§c/world list §4oder " +
                        "\n§c/world location <list | set | remove | tp> <world-name | location-name>" +
                        "\n§c/world <info | upload | delete | tp> <world-name> §4oder " +
                        "\n§c/world set <key> <value> §4oder " +
                        "\n§c/world import <name> <NORMAL | NETHER | THE_END>" +
                        "\n§c/world create <name> [<key>=<value>]..." +
                        "\n§c/world keys (-> Keys for /world set and /world create)"
                );
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        }

        return true;
    }

}

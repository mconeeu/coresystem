/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldCreateProperties;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class WorldCMD extends CorePlayerCommand {

    private enum WorldKey {
        NAME("name"),
        ALIAS("alias"),
        SEED("seed"),
        WORLD_TYPE("worldType", "NORMAL", "FLAT", "LARGE_BIOMES", "AMPLIFIED", "CUSTOMIZED"),
        ENVIRONMENT("environment", "NORMAL", "NETHER", "THE_END"),
        DIFFICULTY("difficulty", "NORMAL", "NETHER", "THE_END"),
        GENERATOR("generator"),
        GENERATOR_SETTINGS("generatorSettings"),
        GENERATE_STRUCUTRES("generateStructures", "true", "false"),
        AUTO_SAVE("autoSave", "true", "false"),
        PVP("pvp", "true", "false"),
        ALLOW_ANIMALS("allowAnimals", "true", "false"),
        ALLOW_MONSTERS("allowMonsters", "true", "false"),
        SPAWN_ANIMALS("spawnAnimals", "true", "false"),
        SPAWN_MONSTERS("spawnMonsters", "true", "false"),
        KEEP_SPAWN_IN_MEMORY("keepSpawnInMemory", "true", "false"),
        LOAD_ON_STARTUP("loadOnStartup", "true", "false");

        private final String key;
        private final String[] values;

        WorldKey(String key, String... values) {
            this.key = key;
            this.values = values;
        }
    }

    private final WorldManager manager;

    public WorldCMD(WorldManager manager) {
        super("world", "system.bukkit.world", "w");
        this.manager = manager;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length < 2) {
            if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
                Msg.send(p, "§7Du befindest dich gerade auf der Welt: §f" + p.getWorld().getName());
                ComponentBuilder componentBuilder = new ComponentBuilder("§7Du kannst dich zu folgenden Welten teleportieren: ");

                List<CoreWorld> worlds = manager.getWorlds();
                for (int i = 0; i < worlds.size(); i++) {
                    CoreWorld world = worlds.get(i);

                    componentBuilder
                            .append("§3" + world.getName())
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + world.getVersionString() + " §7(" + world.getId() + ")\n§7§oLinksklick zum teleportieren").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world tp " + world.getName()));

                    if (i != worlds.size() - 1) componentBuilder.append(ChatColor.GRAY + ", ");
                }

                p.spigot().sendMessage(componentBuilder.create());
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (p.hasPermission("system.bukkit.world.setspawn")) {
                    CoreSystem.getInstance().getCorePlayer(p).getWorld().setSpawnLocation(p.getLocation());
                    Msg.send(p, "§2Der Spawn wurde erfolgreich gesetzt!");
                } else {
                    Msg.sendTransl(p, "system.command.noperm");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("keys")) {
                StringBuilder sb = new StringBuilder("§2Diese Keys können bei §a/world set <key> <value>§2 oder bei §a/world create [<key>=<value>]... §2verwendet werden:");
                for (WorldKey key : WorldKey.values()) {
                    sb.append("\n§7§o").append(key.key).append(" §8: §f§o{").append(key.values.length > 0 ? Arrays.toString(key.values) : key.key).append("}");
                }

                Msg.send(p, sb.toString());
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                manager.reload();
                Msg.send(p, "§2Du hast alle world Configurationen neu geladen!");
                return true;
            } else {
                for (CoreWorld w : manager.getWorlds()) {
                    if (args[0].equalsIgnoreCase(w.getName())) {
                        p.performCommand("world tp " + w.getName());
                        return true;
                    }
                }
            }
        } else {
            if (args[0].equalsIgnoreCase("create")) {
                if (p.hasPermission("system.bukkit.world.create")) {
                    World w = Bukkit.getWorld(args[1]);

                    if (w == null) {
                        Map<String, String> settings = new HashMap<>();

                        for (int i = 2; i < args.length; i++) {
                            String[] setting = args[i].split("=");

                            if (setting.length < 2) {
                                Msg.send(p, "§4Bitte benutze: §c/world create <name> [<key>=<value>]...");
                                return true;
                            } else {
                                settings.put(setting[0], setting[1]);
                            }
                        }

                        try {
                            if (manager.createWorld(args[1], WorldCreateProperties.fromMap(settings), p) != null) {
                                Msg.send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich erstellt!");
                            } else {
                                Msg.send(p, "§4Die Welt " + args[1] + " konnte nicht erstellt werden. Konsole nach Fehlern überprüfen!");
                            }
                        } catch (IllegalArgumentException e) {
                            Msg.send(p, "§4Mindestens eine Einstellung wurde falsch angegeben: \n§7§o" + e.getMessage());
                        }
                    } else {
                        Msg.send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                    }
                } else {
                    Msg.sendTransl(p, "system.command.noperm");
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("purge") || args[0].equalsIgnoreCase("killall")) {
                    if (p.hasPermission("system.bukkit.world.purge")) {
                        if (args[1].equalsIgnoreCase("animals")) {
                            CoreSystem.getInstance().getCorePlayer(p).getWorld().purgeAnimals();
                            Msg.send(p, "§2Es wurden alle Tiere in deiner Welt gelöscht");
                        } else if (args[1].equalsIgnoreCase("monsters")) {
                            CoreSystem.getInstance().getCorePlayer(p).getWorld().purgeMonsters();
                            Msg.send(p, "§2Es wurden alle Monster in deiner Welt gelöscht");
                        } else {
                            Msg.send(p, "§4Bitte benutze: §c/world purge <animals | monsters>");
                        }
                    } else {
                        Msg.sendTransl(p, "system.command.noperm");
                    }
                    return true;
                } else {
                    CoreWorld w = manager.getWorld(args[1]);

                    if ((args[0].equalsIgnoreCase("tp")
                            || args[0].equalsIgnoreCase("info")
                            || args[0].equalsIgnoreCase("upload")
                            || args[0].equalsIgnoreCase("betaupload")
                            || args[0].equalsIgnoreCase("unload")
                            || args[0].equalsIgnoreCase("delete")
                    ) && w != null) {
                        if (args[0].equalsIgnoreCase("tp")) {
                            if (!w.isLoaded()) {
                                WorldManager.LOADING_BAR.send(p);
                                w.load();
                            }

                            p.teleport(w.bukkit().getSpawnLocation());
                            Msg.send(p, "§2Du wurdest zur Welt §a" + w.getName() + "§2 teleportiert!");

                            return true;
                        } else if (args[0].equalsIgnoreCase("info")) {
                            World bw = w.bukkit();
                            p.sendMessage("");
                            Msg.send(p, "§2Bitteschön, Ein paar Infos über die Welt §a" + args[1] + "§2:" +
                                    "\n§7§oalias: §f" + w.getAlias() +
                                    "\n§7§ospawn-location: §f" + bw.getSpawnLocation() +
                                    "\n§7§oseed: §f" + bw.getSeed() +
                                    "\n§7§oworldType: §f" + bw.getWorldType() +
                                    "\n§7§oenvironment: §f" + bw.getEnvironment() +
                                    "\n§7§odifficulty: §f" + bw.getDifficulty() +
                                    "\n§7§ogenerator: §f" + w.getGenerator() +
                                    "\n§7§ogeneratorSettings: §f" + w.getGeneratorSettings() +
                                    "\n§7§ogenerateStructures: §f" + bw.canGenerateStructures() +
                                    "\n§7§oautoSave: §f" + bw.isAutoSave() +
                                    "\n§7§opvp: §f" + bw.getPVP() +
                                    "\n§7§oallowAnimals: §f" + w.isAllowAnimals() +
                                    "\n§7§oallowMonsters: §f" + w.isAllowMonsters() +
                                    "\n§7§ospawnAnimals: §f" + (bw.getAnimalSpawnLimit() > 0) +
                                    "\n§7§ospawnMonsters: §f" + (bw.getMonsterSpawnLimit() > 0) +
                                    "\n§7§okeepSpawnInMemory: §f" + bw.getKeepSpawnInMemory() +
                                    "\n§7§oloadOnStartup: §f" + w.isLoadOnStartup()
                            );

                            return true;
                        } else if (args[0].equalsIgnoreCase("unload")) {
                            if (p.hasPermission("system.bukkit.world.unload")) {
                                w.setLoadOnStartup(false);
                                w.unload(true);
                                manager.getCoreWorlds().remove(w);

                                Msg.send(p, "§2Die Welt wurde erfolgreich entladen! Benutze §a/world import " + w.getName() + " " + w.getEnvironment() + "§2 um sie wieder zu laden!");
                            } else {
                                Msg.sendTransl(p, "system.command.noperm");
                            }
                        } else if (args[0].equalsIgnoreCase("delete")) {
                            if (p.hasPermission("system.bukkit.world.delete")) {
                                if (w.delete(p)) {
                                    Msg.send(p, "§2Die Welt wurde erfolgreich gelöscht!");
                                } else {
                                    Msg.send(p, "§4Es ist ein Fehler beim löschen der Welt aufgetreten!");
                                }
                            } else {
                                Msg.sendTransl(p, "system.command.noperm");
                            }
                            return true;
                        }
                    } else {
                        Msg.send(p, "§4Diese Welt existiert nicht!");
                        return true;
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (p.hasPermission("system.bukkit.world.modify")) {
                        BukkitCoreWorld w = (BukkitCoreWorld) BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();

                        try {
                            if (args[1].equalsIgnoreCase("name")) {
                                if (!Bukkit.getWorlds().get(0).equals(w.bukkit())) {
                                    w.changeName(args[2]);
                                } else {
                                    Msg.send(p, "§4Du kannst nicht den Namen der Hauptwelt verändern!");
                                    return true;
                                }
                            } else if (args[1].equalsIgnoreCase("alias")) {
                                w.setAlias(args[2]);
                            } else if (args[1].equalsIgnoreCase("type")) {
                                w.setWorldType(WorldType.valueOf(args[2]));
                                Msg.send(p, "§6Diese Einstellung tritt erst nach einem Serverneustart in Wirkung!");
                            } else if (args[1].equalsIgnoreCase("environment")) {
                                w.setEnvironment(World.Environment.valueOf(args[2]));
                                Msg.send(p, "§6Diese Einstellung tritt erst nach einem Serverneustart in Wirkung!");
                            } else if (args[1].equalsIgnoreCase("difficulty")) {
                                w.setDifficulty(Difficulty.valueOf(args[2]));
                            } else if (args[1].equalsIgnoreCase("generator")) {
                                w.setGenerator(args[2]);
                                Msg.send(p, "§6Diese Einstellung tritt erst nach einem Serverneustart in Wirkung!");
                            } else if (args[1].equalsIgnoreCase("generatorSettings")) {
                                w.setGeneratorSettings(args[2]);
                                Msg.send(p, "§6Diese Einstellung tritt erst nach einem Serverneustart in Wirkung!");
                            } else if (args[1].equalsIgnoreCase("generateStructures")) {
                                w.setGenerateStructures(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("autoSave")) {
                                w.setAutoSave(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("pvp")) {
                                w.setPvp(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("allowAnimals")) {
                                w.setAllowAnimals(Boolean.parseBoolean(args[2]));
                                if (!Boolean.parseBoolean(args[2])) w.purgeAnimals();
                            } else if (args[1].equalsIgnoreCase("allowMonsters")) {
                                w.setAllowMonsters(Boolean.parseBoolean(args[2]));
                                if (!Boolean.parseBoolean(args[2])) w.purgeMonsters();
                            } else if (args[1].equalsIgnoreCase("spawnAnimals")) {
                                w.setSpawnAnimals(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("spawnMonsters")) {
                                w.setSpawnMonsters(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("keepSpawnInMemory")) {
                                w.setKeepSpawnInMemory(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("loadOnStartup")) {
                                w.setLoadOnStartup(Boolean.parseBoolean(args[2]));
                            } else if (args[1].equalsIgnoreCase("seed")) {
                                Msg.send(p, "§4Der Seed kann nachträglich nicht verändert werden!");
                                return true;
                            } else {
                                Msg.send(p, "§4Diese Einstellung existiert nicht! Benutze §c/world keys§4 für eine Liste aller Keys!");
                                return true;
                            }

                            w.save();
                            Msg.send(p, "§2Deine Einstellungen wurden übernommen!");
                        } catch (IllegalArgumentException e) {
                            Msg.send(p, "§4Diese Einstellung existiert nicht!");
                        }
                    } else {
                        Msg.sendTransl(p, "system.command.noperm");
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
                                    if (manager.importWorld(args[1], World.Environment.valueOf(args[2]), p)) {
                                        Msg.send(p, "§2Die Welt §a" + args[1] + "§2 wurde erfolgreich geladen!");
                                    } else {
                                        Msg.send(p, "§4Die Welt §c" + args[1] + "§4 konnte nicht importiert werden! Weitere Infos in der Konsole.");
                                    }
                                } else {
                                    Msg.send(p, "§4Es existieren nur diese Environments: §cNORMAL§4, §cNETHER§4, §cTHE_END");
                                }
                            } else {
                                Msg.send(p, "§4Ein Weltordner mit dem Namen §c" + args[1] + "§4 existiert nicht!");
                            }
                        } else {
                            Msg.send(p, "§4Eine Welt mit dem Namen §c" + args[1] + "§4 ist bereits geladen!");
                        }
                    } else {
                        Msg.sendTransl(p, "system.command.noperm");
                    }
                    return true;
                }
            }
        }

        Msg.send(p, "§4Bitte benutze: " +
                "\n§c/world <name> " +
                "\n§c/world list §4oder " +
                "\n§c/world <info | unload | delete | tp> <world-name> §4oder " +
                "\n§c/world purge <animals | monsters> §4oder " +
                "\n§c/world set <key> <value> §4oder " +
                "\n§c/world import <name> <NORMAL | NETHER | THE_END>" +
                "\n§c/world create <name> [<key>=<value>]..." +
                "\n§c/world keys (-> Keys for /world set and /world create)"
        );

        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"list", "info", "unload", "delete", "tp", "purge", "set", "import", "create", "keys"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }
            for (CoreWorld world : manager.getWorlds()) {
                if (world.getName().startsWith(search)) {
                    matches.add(world.getName());
                }
            }

            return matches;
        } else if (args.length == 2) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"info", "unload", "delete", "tp"}) {
                if (args[0].equalsIgnoreCase(arg)) {
                    for (CoreWorld world : manager.getWorlds()) {
                        if (world.getName().startsWith(search)) {
                            matches.add(world.getName());
                        }
                    }

                    return matches;
                }
            }

            if (args[0].equalsIgnoreCase("purge")) {
                for (String arg : new String[]{"animals", "monsters"}) {
                    if (arg.startsWith(search)) {
                        matches.add(arg);
                    }
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                for (WorldKey key : WorldKey.values()) {
                    if (key.key.startsWith(search)) {
                        matches.add(key.key);
                    }
                }
            }

            return matches;
        } else if (args.length == 3) {
            String search = args[2];
            List<String> matches = new ArrayList<>();

            if (args[0].equalsIgnoreCase("set")) {
                for (WorldKey key : WorldKey.values()) {
                    if (key.key.equals(args[1])) {
                        for (String value : key.values) {
                            if (value.startsWith(search)) {
                                matches.add(value);
                            }
                        }

                        break;
                    }
                }
            } else if (args[0].equalsIgnoreCase("import")) {
                for (World.Environment env : World.Environment.values()) {
                    if (env.name().startsWith(search)) {
                        matches.add(env.name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                for (WorldKey key : WorldKey.values()) {
                    if (key.key.startsWith(search)) {
                        matches.add(key.key+"=");
                    }
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }
}

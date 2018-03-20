/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.world.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

public class WorldCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("system.bukkit.world")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7Im Moment sind folgende Welten geladen:");
                        StringBuilder sb = new StringBuilder();

                        List<World> worlds = Bukkit.getWorlds();
                        for (int i = 0; i < worlds.size(); i++) {
                            if (WorldLoader.getLoadedWorlds().contains(worlds.get(i))) sb.append("§f§o[Heruntergeladen] ");
                            sb.append("§3§o").append(worlds.get(i).getName());

                            if (i == worlds.size()-1) sb.append("§7, ");
                        }

                        p.sendMessage(sb.toString());
                        return true;
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("load")) {
                        WorldCreator wc = new WorldCreator(args[1]);
                        wc.environment(World.Environment.valueOf(args[2]));
                        wc.type(WorldType.valueOf(args[3]));

                        Bukkit.createWorld(wc); //load world instead create (here a whole new world is created!

                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Die Welt "+args[1]+" wurde erfolgreich geladen!");
                        return true;
                    }
                }

                p.sendMessage("§4Bitte benutze: §c/world <list | load> [<name>] [<NORMAL | NETHER | THE_END>]");
            } else {
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                return true;
            }
        }
        return false;
    }

}

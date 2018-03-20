/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NpcCMD implements CommandExecutor{

    private NpcManager api;

    public NpcCMD(NpcManager api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (p.hasPermission("system.bukkit.npc")) {
                if (args.length >= 3) {
                    if (args[0].equalsIgnoreCase("add")) {
                        StringBuilder line = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            line.append(args[i]);
                            if (i < args.length-1) line.append(" ");
                        }

                        api.addNPC(args[1], p.getLocation(), args[2], line.toString());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2NPC §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                        return true;
                    } else if (args[0].equalsIgnoreCase("update")) {
                        StringBuilder line = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            line.append(args[i]);
                            if (i < args.length-1) line.append(" ");
                        }

                        api.updateNPC(args[1], p.getLocation(), args[2], line.toString());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2NPC §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                        return true;
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        api.removeNPC(args[1]);
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2NPC §f" + args[1] + "§2 erfolgreich gelöscht!");
                        return true;
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        StringBuilder result = new StringBuilder();
                        result.append(CoreSystem.config.getConfigValue("Prefix")).append("§7Diese NPCs sind gerade geladen:\n");
                        int i = api.getNPCs().keySet().size();
                        for (String h : api.getNPCs().keySet()) {
                            result.append("§3§o").append(h);
                            if (i <= 1) continue;
                            result.append("§7, ");
                            i--;
                        }
                        p.sendMessage(result.toString());
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        api.reload();
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2NPCs erfolgreich neu geladen!");
                        return true;
                    }
                }

                p.sendMessage(CoreSystem.config.getConfigValue("Prefix")+"§4Bitte benutze: §c/npc <add | remove | update | list | reload> [<Name>] [<Texture-Name>] [<Display-Name>]");
                return true;
            } else {
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl");
                return true;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }
    }

}

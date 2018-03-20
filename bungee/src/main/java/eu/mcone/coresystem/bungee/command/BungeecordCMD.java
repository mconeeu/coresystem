/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BungeecordCMD extends Command{

	public BungeecordCMD(){
		super("bungeecord", null, "bungee");
	}
	
	public void execute(final CommandSender sender, final String[] args){
        if (args.length == 0) {
            Messager.sendSimple(sender, "");
            Messager.sendSimple(sender, "§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------");
            Messager.send(sender, "§7Entwickelt von §fTwinsterHD §7und §frufi");
            Messager.sendSimple(sender, "§r");
            Messager.sendSimple(sender, "§7§oWir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten.");
            Messager.sendSimple(sender, "§7§oDeshalb sind auch alle von uns verwendeten Plugins ausschließlich selbst entwickelt!");
            Messager.sendSimple(sender, "§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------");
            Messager.sendSimple(sender, "");
        } else if (args[0].equals("reload")) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer)sender;
                if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
                CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());
                if (!p.hasPermission("system.bungee.reload")) {
                    return;
                }
            }

            if (args.length == 1) {
                Messager.send(sender, "§aMySQL-Config wird neu geladen...");
                CoreSystem.sqlconfig.store();

                Messager.send(sender, "§aPermissions werden neu geladen...");
                CoreSystem.getInstance().getPermissionManager().reload();

                Messager.send(sender, "§aNicks werden neu geladen...");
                CoreSystem.getInstance().getNickManager().reload();
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("config")) {
                    Messager.send(sender, "§aMySQL-Config wird neu geladen...");
                    CoreSystem.sqlconfig.store();
                } else if (args[1].equalsIgnoreCase("permissions")) {
                    Messager.send(sender, "§aPermissions werden neu geladen...");
                    CoreSystem.getInstance().getPermissionManager().reload();
                } else if (args[1].equalsIgnoreCase("nick")) {
                    Messager.send(sender, "§aNicks werden neu geladen...");
                    CoreSystem.getInstance().getNickManager().reload();
                }
            }
        }
	}
}

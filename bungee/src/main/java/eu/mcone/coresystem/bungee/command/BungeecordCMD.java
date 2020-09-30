/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BungeecordCMD extends Command {

    public BungeecordCMD() {
        super("bungeecord", null, "bungee");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§7Entwickelt von §fTwinsterHD §7und §frufi");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§r");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§7§oWir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten.");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§7§oDeshalb sind auch alle von uns verwendeten Plugins ausschließlich selbst entwickelt!");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------");
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "");
        } else if (args[0].equals("reload")) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) sender;
                if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId()))
                    return;
                if (!p.hasPermission("system.bungee.reload")) {
                    return;
                }
            }

            if (args.length == 1) {
                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aTranslation-Manager wird neu geladen...");
                BungeeCoreSystem.getInstance().getTranslationManager().reload();

                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aPermissions werden neu geladen...");
                BungeeCoreSystem.getInstance().getPermissionManager().reload();
                for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    p.reloadPermissions();
                }

                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aNicks werden neu geladen...");
                BungeeCoreSystem.getInstance().getNickManager().reload();
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("translations")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aTranslation-Manager wird neu geladen...");
                    BungeeCoreSystem.getInstance().getTranslationManager().reload();
                } else if (args[1].equalsIgnoreCase("permissions")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aPermissions werden neu geladen...");
                    BungeeCoreSystem.getInstance().getPermissionManager().reload();
                } else if (args[1].equalsIgnoreCase("nick")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§aNicks werden neu geladen...");
                    BungeeCoreSystem.getInstance().getNickManager().reload();
                }
            }
        }
    }
}

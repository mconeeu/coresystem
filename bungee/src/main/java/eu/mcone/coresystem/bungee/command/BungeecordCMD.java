/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeecordCMD extends CoreCommand {

    public BungeecordCMD() {
        super("bungeecord", null, "bungee");
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§r" +
                    "\n§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------" +
                    "\n§7Entwickelt von §fTwinsterHD §7und §frufi" +
                    "\n§r" +
                    "\n§7§oWir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten." +
                    "\n§7§oDeshalb sind auch alle von uns verwendeten Plugins ausschließlich selbst entwickelt!" +
                    "\n§8§m---------- §r§3§lMCONE-BungeeCord-System §8§m----------" +
                    "\n"));
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
                BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aTranslation-Manager wird neu geladen...");
                BungeeCoreSystem.getInstance().getTranslationManager().reload();

                BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aPermissions werden neu geladen...");
                BungeeCoreSystem.getInstance().getPermissionManager().reload();
                for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    p.reloadPermissions();
                }

                BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aNicks werden neu geladen...");
                BungeeCoreSystem.getInstance().getNickManager().reload();
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("translations")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aTranslation-Manager wird neu geladen...");
                    BungeeCoreSystem.getInstance().getTranslationManager().reload();
                } else if (args[1].equalsIgnoreCase("permissions")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aPermissions werden neu geladen...");
                    BungeeCoreSystem.getInstance().getPermissionManager().reload();
                } else if (args[1].equalsIgnoreCase("nick")) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§aNicks werden neu geladen...");
                    BungeeCoreSystem.getInstance().getNickManager().reload();
                }
            }
        }
    }
}

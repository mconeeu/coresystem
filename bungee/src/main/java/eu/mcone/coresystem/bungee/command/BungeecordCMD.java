/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class BungeecordCMD extends CoreCommand implements TabExecutor {

    private enum ReloadType {
        TRANSLATIONS("TranslationManager", () -> BungeeCoreSystem.getInstance().getTranslationManager().reload()),
        PERMISSIONS("Permissions", () -> {
            BungeeCoreSystem.getInstance().getPermissionManager().reload();
            for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
                p.reloadPermissions();
            }
        }),
        NICKS("NickManager", () -> BungeeCoreSystem.getInstance().getNickManager().reload());

        private final String name;
        private final Runnable runnable;

        ReloadType(String name, Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }
    }

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
                for (ReloadType reload : ReloadType.values()) {
                    reload(sender, reload);
                }
            } else if (args.length == 2) {
                for (ReloadType reload : ReloadType.values()) {
                    if (args[1].equalsIgnoreCase(reload.name().toLowerCase())) {
                        reload(sender, reload);
                        return;
                    }
                }

                Msg.sendError(sender, "Bitte benutze: ![/bungee reload <translations|permissions|nicks>]");
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("system.bungee.reload")) {
                return ImmutableSet.of("reload");
            }
        } else if (args.length == 2) {
            String search = args[1];
            Set<String> matches = new HashSet<>();

            for (ReloadType reloadType : ReloadType.values()) {
                if (reloadType.name().toLowerCase().startsWith(search)) {
                    matches.add(reloadType.name().toLowerCase());
                }
            }

            return matches;
        }

        return ImmutableSet.of();
    }

    private void reload(CommandSender sender, ReloadType reloadType) {
        Msg.sendSuccess(sender, "!["+reloadType.name+"] wird neu geladen...");
        reloadType.runnable.run();
    }

}

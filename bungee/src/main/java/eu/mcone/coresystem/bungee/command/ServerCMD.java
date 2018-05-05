/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.api.bungee.util.Messager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Map;

public class ServerCMD extends Command implements TabExecutor {

    public ServerCMD() {
        super("server", "system.bungee.server", "s");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length == 0) {
                Messager.send(p, "§7Du befindest dich gerade auf dem Server: §f" + p.getServer().getInfo().getName());
                Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

                ComponentBuilder cb = new ComponentBuilder(ChatColor.GRAY + "Du kannst dich jetzt mit folgenden Servern verbinden: ");
                int i = 0;
                for (ServerInfo si : servers.values()) {
                    cb
                            .append(ChatColor.GREEN + si.getName())
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f"+si.getPlayers().size()+" Spieler\n§7§oLinksklick zum Joinen").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server "+si.getName()));
                    i++;
                    if (i >= servers.values().size()) break;

                    cb.append(TextComponent.fromLegacyText("§7, "));
                }

                p.sendMessage(cb.create());
            } else if (args.length == 1) {
                final String serverName = args[0];
                final ServerInfo si = ProxyServer.getInstance().getServerInfo(serverName);

                if (si != null) {
                    p.connect(si);
                } else {
                    Messager.send(p, "§4Dieser Server existiert nicht!");
                }
            }
        } else {
            Messager.send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        ArrayList<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (ServerInfo si : ProxyServer.getInstance().getServers().values()) {
                result.add(si.getName());
            }
        }

        return result;
    }

}

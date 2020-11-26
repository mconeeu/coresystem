/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Transl;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class MsgCMD extends CorePlayerCommand implements TabExecutor {

    public MsgCMD() {
        super("msg", null, "tell", "whisper");
    }

    static HashMap<UUID, UUID> reply = new HashMap<>();

    public void onPlayerCommand(ProxiedPlayer bp, final String[] args) {
        final CorePlayer p = CoreSystem.getInstance().getCorePlayer(bp);

        if (args.length < 1) {
            BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Bitte Benutze: §c/msg §c<Player | toggle> §c[<Nachricht>]");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            PlayerSettings settings = p.getSettings();

            if (settings.getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                settings.setPrivateMessages(PlayerSettings.Sender.ALL);
                p.updateSettings(settings);

                BungeeCoreSystem.getInstance().getMessenger().sendTransl(p.bungee(), "system.bungee.chat.private.see");
            } else {
                settings.setPrivateMessages(PlayerSettings.Sender.NOBODY);
                p.updateSettings(settings);

                BungeeCoreSystem.getInstance().getMessenger().sendTransl(p.bungee(), "system.bungee.chat.private.dontsee");
            }
        } else {
            final CorePlayer t = BungeeCoreSystem.getInstance().getCorePlayer(args[0]);

            if (t != null) {
                if (!p.equals(t)) {
                    if (!t.getFriendData().getBlocks().contains(p.getUuid()) || p.hasPermission("system.bungee.chat.private.bypass")) {
                        PlayerSettings playerSettings = p.getSettings(), targetSettings = t.getSettings();

                        if (targetSettings.getPrivateMessages().equals(PlayerSettings.Sender.NOBODY) && !p.hasPermission("system.bungee.chat.private.bypass")) {
                            BungeeCoreSystem.getInstance().getMessenger().send(bp, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                        } else if (targetSettings.getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !t.getFriendData().getFriends().containsKey(p.getUuid()) && !p.hasPermission("system.bungee.chat.private.bypass")) {
                            BungeeCoreSystem.getInstance().getMessenger().send(bp, "§c" + args[0] + "§4 hat private Nachrichten nur für Freunde aktiviert!");
                        } else {
                            if (playerSettings.getPrivateMessages().equals(PlayerSettings.Sender.NOBODY) && !t.hasPermission("system.bungee.chat.private.bypass")) {
                                BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                            } else if (playerSettings.getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !p.getFriendData().getFriends().containsKey(t.getUuid()) && !t.hasPermission("system.bungee.chat.private.bypass")) {
                                BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du hast private Nachrichten nur für Freunde aktiviert!");
                            } else {
                                StringBuilder msg = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }

                                bp.sendMessage(TextComponent.fromLegacyText(Transl.get("system.bungee.chat.private.fromme", p).replaceAll("%Msg-Target%", t.getName()) + msg, ChatColor.GRAY));
                                t.bungee().sendMessage(TextComponent.fromLegacyText(Transl.get("system.bungee.chat.private.tome", p).replaceAll("%Msg-Player%", p.getName()) + msg, ChatColor.GRAY));
                                reply.put(t.getUuid(), p.getUuid());
                            }
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().send(bp, "§c" + args[0] + "§4 hat dich blockiert!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du kannst dich nicht selbst anschreiben, Dummkopf!");
                }
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Dieser Spieler ist nicht online!");
            }
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        String search = args[0];
        Set<String> result = new HashSet<>();

        for (UUID friend : CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender).getFriendData().getFriends().keySet()) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if (p != sender && p.getUniqueId().equals(friend) && p.getName().startsWith(search)) {
                    result.add(p.getName());
                }
            }
        }

        return result;
    }

}

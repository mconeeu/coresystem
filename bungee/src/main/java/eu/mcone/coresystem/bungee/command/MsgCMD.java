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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MsgCMD extends CorePlayerCommand implements TabExecutor {

    public MsgCMD() {
        super("msg", null, "tell", "whisper");
    }

    static HashMap<UUID, UUID> reply = new HashMap<>();

    public void onPlayerCommand(ProxiedPlayer bp, final String[] args) {
        final CorePlayer p = CoreSystem.getInstance().getCorePlayer(bp);

        if (args.length < 1) {
            BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§4Bitte Benutze: §c/msg §c<Player | toggle> §c[<Nachricht>]");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                p.getSettings().setPrivateMessages(PlayerSettings.Sender.ALL);
                p.updateSettings();

                BungeeCoreSystem.getInstance().getMessenger().send(p.bungee(), Transl.get("system.bungee.chat.private.see", bp));
            } else {
                p.getSettings().setPrivateMessages(PlayerSettings.Sender.NOBODY);
                p.updateSettings();

                BungeeCoreSystem.getInstance().getMessenger().send(p.bungee(), Transl.get("system.bungee.chat.private.dontsee", bp));
            }
        } else {
            final CorePlayer t = BungeeCoreSystem.getInstance().getCorePlayer(args[0]);
            if (t != null) {
                if (!p.equals(t)) {
                    if (!t.getFriendData().getBlocks().contains(p.getUuid()) || p.hasPermission("system.bungee.chat.private.bypass")) {
                        if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY) && !p.hasPermission("system.bungee.chat.private.bypass")) {
                            BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                        } else if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !t.getFriendData().getFriends().containsKey(p.getUuid()) && !p.hasPermission("system.bungee.chat.private.bypass")) {
                            BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§c" + args[0] + "§4 hat private Nachrichten nur für Freunde aktiviert!");
                        } else {
                            if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY) && !t.hasPermission("system.bungee.chat.private.bypass")) {
                                BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                            } else if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !p.getFriendData().getFriends().containsKey(t.getUuid()) && !t.hasPermission("system.bungee.chat.private.bypass")) {
                                BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§4Du hast private Nachrichten nur für Freunde aktiviert!");
                            } else {
                                StringBuilder msg = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }

                                bp.sendMessage(new TextComponent(Transl.get("system.bungee.chat.private.fromme", p).replaceAll("%Msg-Target%", t.getName()) + msg));
                                t.bungee().sendMessage(new TextComponent(Transl.get("system.bungee.chat.private.tome", p).replaceAll("%Msg-Player%", p.getName()) + msg));
                                reply.put(t.getUuid(), p.getUuid());
                            }
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§c" + args[0] + "§4 hat dich blockiert!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§4Du kannst dich nicht selbst anschreiben, Dummkopf!");
                }
            } else {
                BungeeCoreSystem.getInstance().getMessenger().sendSender(bp, "§4Dieser Spieler ist nicht online!");
            }
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                result.add(p.getName());
            }
        }

        return result;
    }

}

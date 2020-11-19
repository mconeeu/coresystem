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
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReplyCMD extends CorePlayerCommand {

    public ReplyCMD() {
        super("reply", null, "r");
    }

    public void onPlayerCommand(ProxiedPlayer bp, String[] args) {
        CorePlayer p = CoreSystem.getInstance().getCorePlayer(bp);

        if (MsgCMD.reply.containsKey(p.getUuid())) {
            CorePlayer t = CoreSystem.getInstance().getCorePlayer(MsgCMD.reply.get(p.getUuid()));

            if (t != null) {
                if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                    BungeeCoreSystem.getInstance().getMessenger().send(bp, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                } else if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !t.getFriendData().getFriends().containsKey(p.getUuid())) {
                    BungeeCoreSystem.getInstance().getMessenger().send(bp, "§c" + args[0] + "§4 hat private Nachrichten nur für Freunde aktiviert!");
                } else {
                    if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                        BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                    } else if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !p.getFriendData().getFriends().containsKey(t.getUuid())) {
                        BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du hast private Nachrichten nur für Freunde aktiviert!");
                    } else {
                        StringBuilder msg = new StringBuilder();
                        for (String arg : args) {
                            msg.append(arg).append(" ");
                        }

                        MsgCMD.reply.put(t.getUuid(), p.getUuid());

                        bp.sendMessage(TextComponent.fromLegacyText(Transl.get("system.bungee.chat.private.fromme", bp).replaceAll("%Msg-Target%", t.getName()) + msg, ChatColor.GRAY));
                        t.bungee().sendMessage(TextComponent.fromLegacyText(Transl.get("system.bungee.chat.private.tome", t).replaceAll("%Msg-Player%", p.getName()) + msg, ChatColor.GRAY));
                    }
                }
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Dieser Spieler ist nicht online!");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(bp, "§4Du hast keine offene Konversation!");
        }
    }
}

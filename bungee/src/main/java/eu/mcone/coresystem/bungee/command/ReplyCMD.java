/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCMD extends Command {

    public ReplyCMD() {
        super("reply", null, "r");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            BungeeCorePlayer p = CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender);
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUuid()))
                return;

            if (MsgCMD.reply.containsKey(p.getUuid())) {
                BungeeCorePlayer t = CoreSystem.getInstance().getCorePlayer(MsgCMD.reply.get(p.getUuid()));

                if (t != null) {
                    if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                    } else if (t.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !t.getFriendData().getFriends().containsKey(p.getUuid())) {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§c" + args[0] + "§4 hat private Nachrichten nur für Freunde aktiviert!");
                    } else {
                        if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                            BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                        } else if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.FRIENDS) && !p.getFriendData().getFriends().containsKey(t.getUuid())) {
                            BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Du hast private Nachrichten nur für Freunde aktiviert!");
                        } else {
                            StringBuilder msg = new StringBuilder();
                            for (String arg : args) {
                                msg.append(arg).append(" ");
                            }

                            MsgCMD.reply.put(t.getUuid(), p.getUuid());

                            BungeeCoreSystem.getInstance().getMessager().sendSimple(sender, new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.fromme").replaceAll("%Msg-Target%", t.getName()) + msg));
                            t.bungee().sendMessage(new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.tome").replaceAll("%Msg-Player%", p.getName()) + msg));
                        }
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Dieser Spieler ist nicht online!");
                }
            } else {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Du hast keine offene Konversation!");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessager().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}

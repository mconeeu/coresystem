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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MsgCMD extends Command implements TabExecutor {
	
    public MsgCMD(){
        super("msg", null, "tell", "whisper");
    }

    static HashMap<UUID, UUID> reply = new HashMap<>();

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final BungeeCorePlayer p = CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender);
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUuid())) return;

            if (args.length < 1) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Bitte Benutze: §c/msg §c<Player | toggle> §c[<Nachricht>]");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                if (p.getSettings().getPrivateMessages().equals(PlayerSettings.Sender.NOBODY)) {
                    p.getSettings().setPrivateMessages(PlayerSettings.Sender.ALL);
                    p.updateSettings();

                    BungeeCoreSystem.getInstance().getMessager().send(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.see"));
                } else {
                    p.getSettings().setPrivateMessages(PlayerSettings.Sender.NOBODY);
                    p.updateSettings();

                    BungeeCoreSystem.getInstance().getMessager().send(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.dontsee"));
                }
            } else {
                final BungeeCorePlayer t = BungeeCoreSystem.getInstance().getCorePlayer(args[0]);
                if (t != null) {
                    if (p.equals(t)) {
                        if (!t.getFriendData().getBlocks().contains(p.getUuid())) {
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
                                    for (int i = 1; i < args.length; i++) {
                                        msg.append(args[i]).append(" ");
                                    }
                                    BungeeCoreSystem.getInstance().getMessager().sendSimple(sender, new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.fromme", p).replaceAll("%Msg-Target%", t.getName()) + msg));
                                    t.bungee().sendMessage(new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.tome", p).replaceAll("%Msg-Player%", p.getName()) + msg));
                                    reply.put(t.getUuid(), p.getUuid());
                                }
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessager().send(sender, "§c" + args[0] + "§4 hat dich blockiert!");
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Du kannst dich nicht selbst anschreiben, Dummkopf!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Dieser Spieler ist nicht online!");
                }
            }
        } else {
            BungeeCoreSystem.getInstance().getMessager().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                result.add(p.getName());
            }
        }

        return result;
    }

}

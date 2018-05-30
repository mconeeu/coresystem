/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MsgCMD extends Command implements TabExecutor {
	
    public MsgCMD(){
        super("msg", null, "tell", "whisper");
    }

    static HashMap<UUID, UUID> reply = new HashMap<>();
    static ArrayList<UUID> noMSG = new ArrayList<>();

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length < 1) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Bitte Benutze: §c/msg §c<Player | toggle> §c[<Nachricht>]");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                if (noMSG.contains(p.getUniqueId())) {
                    removeToggled(p);
                    BungeeCoreSystem.getInstance().getMessager().send(p, BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.see"));
                } else {
                    addToggled(p);
                    BungeeCoreSystem.getInstance().getMessager().send(p, BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.dontsee"));
                }
            } else {
                final BungeeCorePlayer t = BungeeCoreSystem.getInstance().getCorePlayer(args[0]);
                if (t != null) {
                    if (p != t.bungee()) {
                        if (!t.getBlocks().contains(p.getUniqueId())) {
                            if (!noMSG.contains(p.getUniqueId())) {
                                if (!noMSG.contains(t.getUuid())) {
                                    StringBuilder msg = new StringBuilder();
                                    for (int i = 1; i < args.length; i++) {
                                        msg.append(args[i]).append(" ");
                                    }
                                    BungeeCoreSystem.getInstance().getMessager().sendSimple(p, new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.fromme", CoreSystem.getInstance().getCorePlayer(p)).replaceAll("%Msg-Target%", t.getName()) + msg));
                                    t.bungee().sendMessage(new TextComponent(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.chat.private.tome", CoreSystem.getInstance().getCorePlayer(p)).replaceAll("%Msg-Player%", p.getName()) + msg));
                                    reply.put(t.getUuid(), p.getUniqueId());
                                } else {
                                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                                }
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§c" + args[0] + "§4 hat dich blockiert!");
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du kannst dich nicht selbst anschreiben, Dummkopf!");
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

    private static void addToggled(ProxiedPlayer p) {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET msg_toggle = 1 WHERE uuid = '" + p.getUniqueId().toString() + "'");
        noMSG.add(p.getUniqueId());
    }

    private static void removeToggled(ProxiedPlayer p) {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET msg_toggle = 0 WHERE uuid = '" + p.getUniqueId().toString() + "'");
        noMSG.remove(p.getUniqueId());
    }

    public static void updateToggled() {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT uuid, msg_toggle FROM userinfo", rs -> {
            try {
                noMSG = new ArrayList<>();
                while (rs.next()) {
                    if (rs.getBoolean("msg_toggle")) noMSG.add(UUID.fromString(rs.getString("uuid")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

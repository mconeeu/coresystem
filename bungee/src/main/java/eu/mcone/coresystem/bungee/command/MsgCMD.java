/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
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
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (args.length < 1) {
                Messager.send(sender, "§4Bitte Benutze: §c/msg §c<Player | toggle> §c[<Nachricht>]");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                if (noMSG.contains(p.getUniqueId())) {
                    removeToggled(p);
                    Messager.send(p, "§2Du hast private Nachrichten wieder aktiviert!");
                } else {
                    addToggled(p);
                    Messager.send(p, "§2Du hast private Nachrichten deaktiviert!");
                }
            } else {
                final CorePlayer t = CoreSystem.getCorePlayer(args[0]);
                if (t != null) {
                    if (p != t) {
                        if (!t.getBlocks().contains(p.getUniqueId())) {
                            if (!noMSG.contains(p.getUniqueId())) {
                                if (!noMSG.contains(t.getUuid())) {
                                    StringBuilder msg = new StringBuilder();
                                    for (int i = 1; i < args.length; i++) {
                                        msg.append(args[i]).append(" ");
                                    }
                                    Messager.sendSimple(p, new TextComponent(CoreSystem.sqlconfig.getConfigValue("Msg-Target").replaceAll("%Msg-Target%", t.getName()) + msg));
                                    t.bungee().sendMessage(new TextComponent(CoreSystem.sqlconfig.getConfigValue("Msg-Player").replaceAll("%Msg-Player%", p.getName()) + msg));
                                    reply.put(t.getUuid(), p.getUniqueId());
                                } else {
                                    Messager.send(sender, "§c" + args[0] + "§4 hat private Nachrichten deaktiviert!");
                                }
                            } else {
                                Messager.send(sender, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                            }
                        } else {
                            Messager.send(p, "§c" + args[0] + "§4 hat dich blockiert!");
                        }
                    } else {
                        Messager.send(p, "§4Du kannst dich nicht selbst anschreiben, Dummkopf!");
                    }
                } else {
                    Messager.send(sender, "§4Dieser Spieler ist nicht online!");
                }
            }
        } else {
            Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
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
        CoreSystem.mysql1.update("UPDATE userinfo SET msg_toggle = 1 WHERE uuid = '" + p.getUniqueId().toString() + "'");
        noMSG.add(p.getUniqueId());
    }

    private static void removeToggled(ProxiedPlayer p) {
        CoreSystem.mysql1.update("UPDATE userinfo SET msg_toggle = 0 WHERE uuid = '" + p.getUniqueId().toString() + "'");
        if (noMSG.contains(p.getUniqueId())) noMSG.remove(p.getUniqueId());
    }

    public static void updateToggled() {
        CoreSystem.mysql1.select("SELECT uuid, msg_toggle FROM userinfo", rs -> {
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

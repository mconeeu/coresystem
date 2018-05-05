/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCMD extends Command{

	public ReplyCMD(){
	    super("reply", null, "r");
	  }
	  
    public void execute(final CommandSender sender, final String[] args){
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (MsgCMD.reply.containsKey(p.getUniqueId())) {
                ProxiedPlayer t = ProxyServer.getInstance().getPlayer(MsgCMD.reply.get(p.getUniqueId()));

                if (t != null) {
                    if (!MsgCMD.noMSG.contains(p.getUniqueId())) {
                        if (!MsgCMD.noMSG.contains(t.getUniqueId())) {
                            StringBuilder msg = new StringBuilder();
                            for (String arg : args) {
                                msg.append(arg).append(" ");
                            }

                            MsgCMD.reply.put(t.getUniqueId(), p.getUniqueId());

                            Messager.sendSimple(p, new TextComponent(BungeeCoreSystem.sqlconfig.getConfigValue("Msg-Target").replaceAll("%Msg-Target%", t.getName()) + msg));
                            t.sendMessage(new TextComponent(BungeeCoreSystem.sqlconfig.getConfigValue("Msg-Player").replaceAll("%Msg-Player%", p.getName()) + msg));
                        } else {
                            Messager.send(p, "§c" + t.getName() + "§4 hat private Nachrichten deaktiviert!");
                        }
                    } else {
                        Messager.send(sender, "§4Du hast private Nachrichten §cdeaktiviert§4!");
                    }
                } else {
                    Messager.send(p, "§4Dieser Spieler ist nicht online!");
                }
            } else {
                Messager.send(p, "§4Du hast keine offene Konversation!");
            }
        } else {
            Messager.sendSimple(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }
}

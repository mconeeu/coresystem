/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeamChatCMD extends Command{
	
    public TeamChatCMD(){
    super("teamchat", "system.bungee.teamchat", "tc");
    }

    public void execute(final CommandSender sender, final String[] args){
        if (sender instanceof ProxiedPlayer){
            final ProxiedPlayer p = (ProxiedPlayer)sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (p.hasPermission("system.bungee.teamchat")){
                if (args.length == 0){
                    Messager.send(p, "§4Bitte benutze: §c/tc <Nachricht>");
                }else{
                    String message = "";
                    for (int i = 0; i < args.length; i++) {
                        message = message + args[i] + " ";
                    }
                    for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                        if (all.hasPermission("system.bungee.teamchat") || all.hasPermission("System.bungee.*")) {
                          Messager.sendSimple(all, CoreSystem.sqlconfig.getConfigValue("TeamChat-Prefix").replaceAll("%Playername%", CoreSystem.getCorePlayer(p).getMainGroup().getPrefix() + p.getDisplayName()) + message);
                        }
                    }
                }
            }else{
                Messager.sendSimple(p, CoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
            }
        }else{
          Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }
}

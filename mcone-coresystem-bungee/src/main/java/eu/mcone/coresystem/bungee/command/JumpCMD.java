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
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class JumpCMD extends Command{

    public JumpCMD(){
	    super("jump", "system.bungee.jump");
	  }
  
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (p.hasPermission("system.bungee.jump")) {
                if (args.length == 1) {
                    ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);

                    if (t != null) {
                        if (BungeeCoreSystem.getInstance().getCorePlayer(p).getFriends().containsKey(t.getUniqueId())) {
                            ServerInfo tserver = t.getServer().getInfo();

                            if (t.getServer().getInfo() != p.getServer().getInfo()) {
                                p.connect(tserver);

                                Messager.send(p, "§7Du bist zu §f" + t.getName() + "§7 gesprungen!");
                            } else {
                                Messager.send(p, "§4Du bist bereits auf diesem Server!");
                            }
                        } else {
                            Messager.send(p, "§4Du kannst nur zu Spielern springen die deine Freunde sind!");
                        }
                    } else {
                        Messager.send(p, BungeeCoreSystem.sqlconfig.getConfigValue("System-No-Online-Player"));
                    }
                } else {
                    Messager.send(p, "§cBenutze: /jump <Name>!");
                }
            } else {
                Messager.sendSimple(p, new TextComponent(BungeeCoreSystem.sqlconfig.getConfigValue("System-No-Perm")));
            }
        } else {
            Messager.sendSimple(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }
}

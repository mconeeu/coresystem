/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnnickCMD extends Command{

    public UnnickCMD() {
        super("unnick", "system.bungee.nick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            BungeeCorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

            if (args.length == 0) {
                if (cp.isNicked()) {
                    BungeeCoreSystem.getInstance().getNickManager().unnick(p);
                } else {
                    Messager.send(p, "§4Du bist nicht genickt!");
                }
            } else {
                Messager.send(p, "§4Bitte benutze: §c/unnick");
            }
        }
    }

}

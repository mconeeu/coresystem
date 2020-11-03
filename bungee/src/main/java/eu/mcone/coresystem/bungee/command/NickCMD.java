/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NickCMD extends CorePlayerCommand {

    public NickCMD() {
        super("nick", "system.bungee.nick");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0) {
            if (!cp.isNicked()) {
                BungeeCoreSystem.getInstance().getNickManager().nick(p);
            } else {
                BungeeCoreSystem.getInstance().getNickManager().unnick(p);
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (p.hasPermission("group.developer")) {
                BungeeCoreSystem.getInstance().getMessenger().send(p, "§aDie Nicks wurden erfolgreich neu geladen");
                BungeeCoreSystem.getInstance().getNickManager().reload();
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du hast keine Berechtigung für diesen Befehl!");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/nick");
        }
    }

}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnnickCMD extends CorePlayerCommand {

    public UnnickCMD() {
        super("unnick", "system.bungee.nick");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (CoreSystem.getInstance().getCorePlayer(p).isNicked()) {
            BungeeCoreSystem.getInstance().getNickManager().unnick(p);
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du bist nicht genickt!");
        }
    }

}

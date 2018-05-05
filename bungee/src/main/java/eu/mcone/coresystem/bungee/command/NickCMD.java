/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.api.bungee.util.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NickCMD extends Command{

    public NickCMD() {
        super("nick", "system.bungee.nick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            BungeeCorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length == 0) {
                if (!cp.isNicked()) {
                    BungeeCoreSystem.getInstance().getNickManager().nick(p);
                } else {
                    BungeeCoreSystem.getInstance().getNickManager().unnick(p);
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("group.Developer")) {
                    Messager.send(p, "§aDie Nicks wurden erfolgreich neu geladen");
                    BungeeCoreSystem.getInstance().getNickManager().reload();
                } else {
                    Messager.send(p, "§4Du hast keine Berechtigung für diesen Befehl!");
                }
            } else {
                Messager.send(p, "§4Bitte benutze: §c/nick");
            }
        }
    }

}

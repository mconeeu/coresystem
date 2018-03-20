/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
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
            CorePlayer cp = CoreSystem.getCorePlayer(p);
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (args.length == 0) {
                if (!cp.isNicked()) {
                    CoreSystem.getInstance().getNickManager().nick(p);
                } else {
                    CoreSystem.getInstance().getNickManager().unnick(p);
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("group.Developer")) {
                    Messager.send(p, "§aDie Nicks wurden erfolgreich neu geladen");
                    CoreSystem.getInstance().getNickManager().reload();
                } else {
                    Messager.send(p, "§4Du hast keine Berechtigung für diesen Befehl!");
                }
            } else {
                Messager.send(p, "§4Bitte benutze: §c/nick");
            }
        }
    }

}

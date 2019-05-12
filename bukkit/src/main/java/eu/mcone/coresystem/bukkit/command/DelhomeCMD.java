/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.util.Messager;
import org.bukkit.entity.Player;

public class DelhomeCMD extends CorePlayerCommand {

    private final Messager messager;

    public DelhomeCMD(CorePlugin plugin) {
        super("delhome", null, "deletehome", "remhome", "removehome");
        this.messager = plugin.getMessager();
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

            if (cp.getHomes().containsKey(args[0])) {
                cp.removeHome(args[0]);
                messager.send(p, "§2Dein Home §a"+args[0]+"§2 wurde erfolgreich gelöscht!");
            } else {
                messager.send(p, "§4Du hast kein Home mit dem Namen §c"+args[0]+"§4!");
            }

            return true;
        }

        CoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/delhome <name>");
        return false;
    }

}
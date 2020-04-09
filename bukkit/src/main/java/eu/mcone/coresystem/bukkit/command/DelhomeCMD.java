/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.util.Messager;
import org.bukkit.entity.Player;

public class DelhomeCMD extends CorePlayerCommand {

    private final Messager messager;
    private final HomeManagerGetter apiGetter;

    public DelhomeCMD(CorePlugin plugin, HomeManagerGetter apiGetter) {
        super("delhome", null, "deletehome", "remhome", "removehome");
        this.messager = plugin.getMessager();
        this.apiGetter = apiGetter;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);

        if (args.length == 1) {
            if (api.getHomes().containsKey(args[0])) {
                api.removeHome(args[0]);
                messager.sendTransl(p, "system.home.delete", args[0]);
            } else {
                messager.sendTransl(p, "system.home.null", args[0]);
            }

            return true;
        }

        CoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/delhome <name>");
        return false;
    }

}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.chat.Messenger;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelhomeCMD extends CorePlayerCommand {

    private final Messenger messager;
    private final HomeManagerGetter apiGetter;

    public DelhomeCMD(CorePlugin plugin, HomeManagerGetter apiGetter) {
        super("delhome", null, "deletehome", "remhome", "removehome");
        this.messager = plugin.getMessenger();
        this.apiGetter = apiGetter;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);

        if (args.length == 1) {
            if (api.getHomes().containsKey(args[0])) {
                api.removeHome(args[0]);
                messager.sendTransl(p, "system.bukkit.home.delete", args[0]);
            } else {
                messager.sendTransl(p, "system.bukkit.home.null", args[0]);
            }

            return true;
        }

        Msg.send(p, "§4Bitte benutze: §c/delhome <name>");
        return false;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);
        String search = args[0];
        List<String> matches = new ArrayList<>();

        for (String home : api.getHomes().keySet()) {
            if (home.startsWith(search)) {
                matches.add(home);
            }
        }

        return matches;
    }
}

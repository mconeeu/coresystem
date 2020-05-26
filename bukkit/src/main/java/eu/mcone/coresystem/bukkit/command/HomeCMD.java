/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class HomeCMD extends CorePlayerCommand {

    private final Messenger messager;
    private final HomeManagerGetter apiGetter;
    private final int cooldown;

    public HomeCMD(CorePlugin plugin, HomeManagerGetter apiGetter, int cooldown) {
        super("home", "system.bukkit.home", "homes");
        this.messager = plugin.getMessenger();
        this.apiGetter = apiGetter;
        this.cooldown = cooldown;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0) {
            Iterator<Map.Entry<String, Location>> it = api.getHomes().entrySet().iterator();

            if (it.hasNext()) {
                Map.Entry<String, Location> home = it.next();

                messager.send(p, "§2Teleportiere zu Home §a" + home.getKey());
                cp.teleportWithCooldown(home.getValue(), cooldown);
            } else {
                messager.send(p, "§7§oDu hast noch keine Homes auf diesem Server!");
            }

            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                p.performCommand("listhomes");
            } else if (args[0].equalsIgnoreCase("help")) {
                messager.send(p, "§4Bitte benutze; §c/home [<name> | list | set | delete]");
            } else {
                Location home = api.getHome(args[0]);

                if (home != null) {
                    messager.send(p, "§2Teleportiere zu Home §a" + args[0]);
                    cp.teleportWithCooldown(home, cooldown);
                } else {
                    messager.send(p, "§4Du hast kein Home mit dem Namen §c" + args[0]);
                }
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                p.performCommand("sethome " + args[1]);
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
                p.performCommand("delhome " + args[1]);
            }
        }

        messager.send(p, "§4Bitte benutze; §c/home [<name> | list | set | delete]");
        return true;
    }

}

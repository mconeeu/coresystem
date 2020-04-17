/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

public class SethomeCMD extends CorePlayerCommand {

    private final Messenger messager;
    private final HomeManagerGetter apiGetter;
    
    public SethomeCMD(CorePlugin plugin, HomeManagerGetter apiGetter) {
        super("sethome");
        this.messager = plugin.getMessenger();
        this.apiGetter = apiGetter;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
                messager.send(p, "§4Dein Home darf nicht §c"+args[0].toLowerCase()+"§4 heißen!");
            } else if (args[0].length() <= 16) {
                CorePlayer cp = BukkitCoreSystem.getSystem().getCorePlayer(p);
                int maxHomes = 0;

                for (int i = 50; i > 0; i--) {
                    if (cp.hasPermission("system.bukkit.home."+i)) {
                        maxHomes = i;
                        break;
                    }
                }

                if (maxHomes == 0) {
                    messager.sendTransl(p, "system.command.noperm");
                } else if (api.getHomes().size() < maxHomes) {
                    api.setHome(args[0], p.getLocation());
                    messager.send(p, "§2Dein Home §a"+args[0]+"§2 wurde erfolgreich gesetzt!");
                } else {
                    messager.send(p, "§4Du hast bereits die maximale Anzahl von §c"+maxHomes+" Homes§4 verwendet! Benutze §c/delhome <name>§4 um ein Home zu löschen!");
                }
            } else {
                messager.send(p, "§4Der Name deines Homes darf nicht länger als 16 Zeichen sein!");
            }

            return true;
        }

        messager.send(p, "§4Bitte benutze: §c/sethome <name>");
        return true;
    }

}

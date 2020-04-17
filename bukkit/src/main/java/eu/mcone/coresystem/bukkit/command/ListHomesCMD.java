/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class ListHomesCMD extends CorePlayerCommand {

    private final Messenger messager;
    private final HomeManagerGetter apiGetter;

    public ListHomesCMD(CorePlugin plugin, HomeManagerGetter apiGetter) {
        super("listhomes", null, "listhome", "homelist", "lhome", "lhomes");
        this.messager = plugin.getMessenger();
        this.apiGetter = apiGetter;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        HomeManager api = apiGetter.getHomeManager(p);

        if (args.length == 0) {
            Map<String, Location> homes = api.getHomes();

            if (homes.size() > 0) {
                messager.send(p, "§7Du hast folgende Homes auf diesem Server: ");
                ComponentBuilder componentBuilder = new ComponentBuilder("");

                for (Map.Entry<String, Location> home : homes.entrySet()) {
                    componentBuilder
                            .append(home.getKey())
                            .color(ChatColor.DARK_AQUA)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(home.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.getKey()))
                            .append(", ")
                            .color(ChatColor.GRAY);
                }

                p.spigot().sendMessage(componentBuilder.create());
            } else {
                messager.send(p, "§7§oDu hast noch keine Homes auf diesem Server!");
            }

            return true;
        }

        messager.send(p, "§4Bitte benutze: §c/listhomes");
        return false;
    }

}

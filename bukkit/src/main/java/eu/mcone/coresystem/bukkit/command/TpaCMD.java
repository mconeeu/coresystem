/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.broadcast.Messenger;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Transl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpaCMD extends CorePlayerCommand {

    private final Messenger messager;

    static Map<String, List<String>> players = new HashMap<>();

    public TpaCMD(CorePlugin plugin) {
        super("tpa");
        this.messager = plugin.getMessenger();
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                if (players.containsKey(p.getName()) && players.get(p.getName()).contains(t.getName())) {
                    messager.send(p, "§4Du hast diesem Spieler bereits eine Teleportanfrage geschickt!");
                } else if (t == p) {
                    messager.send(p, "§4Du kannst dir nicht selbst eine Teleportanfrage schicken!");
                } else {
                    List<String> requests = players.getOrDefault(p.getName(), new ArrayList<>());
                    requests.add(t.getName());
                    players.put(p.getName(), requests);

                    messager.send(t, "§7Du hast eine Teleportanfrage von §f" + p.getName() + "§7 erhalten!");
                    t.spigot().sendMessage(new ComponentBuilder(Transl.get("build.prefix", t))
                            .append("§a[Annehmen]")
                            .color(ChatColor.DARK_GREEN)
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + p.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(p.getName() + " zu mir teleportieren").color(ChatColor.GRAY).create()))
                            .append(" §c[Ablehnen]")
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + p.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(p.getName() + " ablehnen").color(ChatColor.GRAY).create()))
                            .create()
                    );
                    messager.send(p, "§2Du hast §a" + args[0] + "§2 eine Teleportanfrage geschickt!");
                }
            } else {
                messager.send(p, "§4Dieser Spieler ist nicht online!");
            }

            return true;
        }

        messager.send(p, "§4Bitte benutze: §c/tpa <Spieler>");
        return true;
    }

}

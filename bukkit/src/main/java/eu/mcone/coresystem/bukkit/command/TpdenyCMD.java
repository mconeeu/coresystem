/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.broadcast.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpdenyCMD extends CorePlayerCommand {

    private final Messenger messager;

    public TpdenyCMD(CorePlugin plugin) {
        super("tpdeny", null, "tpadeny");
        this.messager = plugin.getMessenger();
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                if (TpaCMD.players.containsKey(t.getName()) && TpaCMD.players.get(t.getName()).contains(p.getName())) {
                    messager.send(p, "§7Du hast die Teleportanfrage von §f" + t.getName() + "§7 abgelehnt!");
                    messager.send(t, "§f" + args[0] + "§7 hat deine Anfrage §cabgelehnt§7!");
                    TpaCMD.players.get(t.getName()).remove(p.getName());
                } else {
                    messager.send(p, "§4Dieser Spieler hat dir keine Teleportanfrage geschickt!");
                }
            } else {
                messager.send(p, "§4Dieser Spieler ist nicht online!");
            }

            return true;
        }

        messager.send(p, "§4Bitte benutze: §c/tpdeny <Spieler>");
        return true;
    }

}

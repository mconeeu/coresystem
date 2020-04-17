/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpacceptCMD extends CorePlayerCommand {

    private final Messenger messager;
    private final int cooldown;

    public TpacceptCMD(CorePlugin plugin, int cooldown) {
        super("tpaccept", null, "tpaaccept");
        this.messager = plugin.getMessenger();
        this.cooldown = cooldown;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                if (TpaCMD.players.containsKey(t.getName()) && TpaCMD.players.get(t.getName()).contains(p.getName())) {
                    messager.send(p, "§2Du hast die Teleportanfrage von " + t.getName() + " angenommen! Teleportiere...");
                    messager.send(t, "§a" + p.getName() + "§2 hat deine Anfrage angenommen! Du wirst teleportiert...");

                    CoreSystem.getInstance().getCorePlayer(t).teleportWithCooldown(p.getLocation(), cooldown);
                    TpaCMD.players.get(t.getName()).remove(p.getName());
                } else {
                    messager.send(p, "§4Dieser Spieler hat dir keine Teleportanfrage geschickt!");
                }
            } else {
                messager.send(p, "§4Dieser Spieler ist nicht online!");
            }

            return true;
        }

        messager.send(p, "§4Bitte benutze: §c/tpaccept <Spieler>");
        return true;
    }

}

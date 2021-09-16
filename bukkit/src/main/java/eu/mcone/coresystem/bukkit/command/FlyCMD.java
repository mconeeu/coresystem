/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCMD extends CorePlayerCommand {

    private static final List<UUID> FLY_MODE_LIST = new ArrayList<>();

    public FlyCMD() {
        super("fly", "system.bukkit.fly");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            if (FLY_MODE_LIST.contains(p.getUniqueId())) {
                p.setAllowFlight(false);
                p.setFlying(false);
                FLY_MODE_LIST.remove(p.getUniqueId());
                Msg.send(p, "§2Du hast den §fFlugmodus §2deaktiviert!");
            } else {
                if (!CoreSystem.getInstance().getCorePlayer(p).isNicked()) {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    FLY_MODE_LIST.add(p.getUniqueId());
                    Msg.send(p, "§2Du hast den §fFlugmodus §2aktiviert!");
                } else {
                    Msg.sendError(p, "Du kannst den Flugmodus nicht benutzen, während du genickt bist!");
                }
            }
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                if (FLY_MODE_LIST.contains(t.getUniqueId())) {
                    t.setAllowFlight(false);
                    t.setFlying(false);
                    FLY_MODE_LIST.remove(t.getUniqueId());
                    Msg.send(p, "§2Du hast den §fFlugmodus §2für §f" + t.getName() + " §2deaktiviert!");
                } else {
                    if (!CoreSystem.getInstance().getCorePlayer(t).isNicked()) {
                        t.setAllowFlight(true);
                        t.setFlying(true);
                        FLY_MODE_LIST.add(t.getUniqueId());
                        Msg.send(p, "§2Du hast den §fFlugmodus §2für §f" + t.getName() + " §2aktiviert!");
                    } else {
                        Msg.sendError(p, "Der Flugmodus für "+t.getName()+" konnte nicht aktiviert werden, da der Spieler genickt ist!");
                    }
                }
            } else {
                Msg.send(p, "§4Der Spieler §c" + args[0] + " §4konnte nicht gefunden werden");
            }
        } else {
            Msg.send(p, "§4Bitte benutze: §c/fly §4oder §c/fly {spieler}");
        }

        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        String search = args[0];
        List<String> matches = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != p && player.getName().startsWith(search)) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

}

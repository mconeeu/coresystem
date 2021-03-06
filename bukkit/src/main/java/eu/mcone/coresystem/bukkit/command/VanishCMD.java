/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCMD extends CorePlayerCommand {

    public VanishCMD() {
        super("vanish", "system.bukkit.vanish", "v");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            setVanished(BukkitCoreSystem.getSystem().getCorePlayer(p));
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                setVanished(BukkitCoreSystem.getSystem().getCorePlayer(t));
                Msg.send(p, "§2Der Vanish Modus von §a" + t.getName() + "§2 wurde geändert!");
            } else {
                Msg.send(p, "§4Der Spieler ist nicht online!");
            }
        } else {
            Msg.send(p, "§4Bitte benutze §c/vanish [<name>]");
        }

        return false;
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

    private static void setVanished(CorePlayer cp) {
        if (!cp.isVanished()) {
            cp.setVanished(true);

            if (VanishChatCMD.chatEnabled.contains(cp.getUuid())) {
                Msg.sendInfo(cp.bukkit(), "![Achtung]: Der Vanish Chat ist aktiviert!");
            }

        } else {
            cp.setVanished(false);
        }
    }

}

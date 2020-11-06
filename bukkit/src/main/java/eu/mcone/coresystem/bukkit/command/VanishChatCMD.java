/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

import java.util.*;

public class VanishChatCMD extends CorePlayerCommand {

    public static Set<UUID> chatEnabled = new HashSet<>();

    public VanishChatCMD() {
        super("vanishchat", "system.bukkit.vanish", "vc");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (CoreSystem.getInstance().getCorePlayer(p).isVanished()) {
            if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                if (args[0].equalsIgnoreCase("on")) {
                    chatEnabled.add(p.getUniqueId());
                    BukkitCoreSystem.getInstance().getMessenger().sendSuccess(p, "Du hast den ![Vanish Chat] aktiviert!");

                    return true;
                } else if (args[0].equalsIgnoreCase("off")) {
                    chatEnabled.remove(p.getUniqueId());
                    BukkitCoreSystem.getInstance().getMessenger().sendSuccess(p, "Du hast den ![Vanish Chat] deaktiviert!");

                    return true;
                }
            }

            BukkitCoreSystem.getInstance().getMessenger().sendError(p, "Bitte benutze: ![/vc <on|off>]");
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Benutze §c/vanish§4 um in den Vanish-Modus zu wechseln!");
        }

        return false;
    }

    private static void setVanished(CorePlayer cp) {
        if (!cp.isVanished()) {
            cp.setVanished(true);
        } else {
            cp.setVanished(false);
        }
    }

}

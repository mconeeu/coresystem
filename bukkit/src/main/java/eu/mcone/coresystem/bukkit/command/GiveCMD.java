/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveCMD extends CoreCommand {

    public GiveCMD() {
        super("give", "system.bukkit.give");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                givePlayerItem(sender, p, args[0]);
            } else {
                Msg.sendTransl(sender, "system.command.consolesender");
                return false;
            }
        } else if (args.length == 2) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                givePlayerItem(sender, t, args[1]);
            } else {
                Msg.send(sender, "§4Der Spieler §c" + args[0] + " §4konnte nicht gefunden werden!");
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        String search = args[args.length-1];
        List<String> matches = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != sender && player.getName().startsWith(search)) {
                    matches.add(player.getName());
                }
            }
        }

        for (Material mat : Material.values()) {
            if (mat.name().toLowerCase().startsWith(search)) {
                matches.add(mat.name().toLowerCase());
            }
        }

        return matches;
    }

    private static void givePlayerItem(CommandSender sender, Player p, String arg) {
        Material material = resolveItem(arg);

        if (material != null) {
            p.getInventory().addItem(new ItemStack(material));
            Msg.send(sender, "§2Du hast das Item §a" + material.toString() + " §2erhalten!");
        } else {
            Msg.send(sender, "§4Das Item §c" + arg + " §4konnte nicht gefunden werden!");
        }
    }

    private static Material resolveItem(String arg) {
        try {
            int id = Integer.parseInt(arg);
            return Material.getMaterial(id);
        } catch (NumberFormatException ignored) {
            return Material.getMaterial(arg.replace("minecraft.", "").toUpperCase());
        }
    }

}

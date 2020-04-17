/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                CoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.consolesender");
                return false;
            }
        } else if (args.length == 2) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                givePlayerItem(sender, t, args[1]);
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(sender, "§4Der Spieler §c" + args[0] + " §4konnte nicht gefunden werden!");
            }
        }

        return false;
    }

    private static void givePlayerItem(CommandSender sender, Player p, String arg) {
        Material material = resolveItem(arg);

        if (material != null) {
            p.getInventory().addItem(new ItemStack(material));
            BukkitCoreSystem.getInstance().getMessenger().send(sender, "§2Du hast das Item §a" + material.toString() + " §2erhalten!");
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(sender, "§4Das Item §c" + arg + " §4konnte nicht gefunden werden!");
        }
    }

    private static Material resolveItem(String arg) {
        try {
            int id = Integer.parseInt(arg);
            return Material.getMaterial(id);
        } catch (NumberFormatException ignored) {
            return Material.getMaterial(arg.toUpperCase());
        }
    }

}

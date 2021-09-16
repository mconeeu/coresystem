/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.command;

import eu.mcone.coresystem.api.bukkit.facades.Msg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CorePlayerCommand extends CoreCommand {

    public CorePlayerCommand(String name) {
        super(name);
    }

    public CorePlayerCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return onPlayerCommand((Player) sender, args);
        } else {
            Msg.sendTransl(sender, "system.command.consolesender");
            return false;
        }
    }

    public abstract boolean onPlayerCommand(Player p, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return onPlayerTabComplete((Player) sender, args);
        } else return null;
    }

    public List<String> onPlayerTabComplete(Player p, String[] args) {
        return null;
    }

}

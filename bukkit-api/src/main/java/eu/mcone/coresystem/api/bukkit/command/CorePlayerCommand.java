/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            CoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
            return false;
        }
    }

    public abstract boolean onPlayerCommand(Player p, String[] args);

}

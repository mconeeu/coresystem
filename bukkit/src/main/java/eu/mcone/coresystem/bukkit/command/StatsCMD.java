/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.inventory.StatsInventory;
import org.bukkit.entity.Player;

public class StatsCMD extends CorePlayerCommand {

    public StatsCMD() {
        super("stats");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        new StatsInventory(p).openInventory();
        return true;
    }

}

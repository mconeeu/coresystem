/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.inventory.ProfileInventory;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfilCMD implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (args.length == 0) {
                new ProfileInventory(CoreSystem.getCorePlayer(p));
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        return true;
    }
}

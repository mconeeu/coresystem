/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.listener.ChatListener;
import org.bukkit.command.CommandSender;

public class SlowchatCMD extends CoreCommand {

    private static final int DEFAULT_COOLDOWN = 3;

    public SlowchatCMD() {
        super("slowchat", "system.bukkit.chat.slowchat", "sc");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            BukkitCoreSystem.getInstance().getMessenger().send(sender, "§2Der Slowchat-Modus ist aktiviert. §a"+ChatListener.getCooldown()+"s Cooldown");
            return true;
        } else if (args.length == 1 && !args[0].equalsIgnoreCase("help")) {
            if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("off")) {
                ChatListener.setCooldown(0);
                BukkitCoreSystem.getInstance().getMessenger().send(sender, "§2Du hast den Slowchat-Modus deaktiviert!");

                return true;
            } else if (args[0].equalsIgnoreCase("on")) {
                ChatListener.setCooldown(DEFAULT_COOLDOWN);
                BukkitCoreSystem.getInstance().getMessenger().send(sender, "§2Der Slowchat-Modus wurde auf §a"+DEFAULT_COOLDOWN+"s§2 gesetzt!");

                return true;
            } else {
                try {
                    int cooldown = Integer.parseInt(args[0]);
                    ChatListener.setCooldown(cooldown);
                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§2Der Slowchat-Modus wurde auf §a"+cooldown+"s§2 gesetzt!");

                    return true;
                } catch (NumberFormatException ignored) {}
            }
        }

        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze: §c/slowchat [<on|off|seconds>]");
        return false;
    }

}

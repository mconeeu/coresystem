/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.bukkit.listener.ChatListener;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlowchatCMD extends CoreCommand {

    private static final int DEFAULT_COOLDOWN = 3;

    public SlowchatCMD() {
        super("slowchat", "system.bukkit.chat.slowchat", "sc");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Msg.send(sender, "§2Der Slowchat-Modus ist aktiviert. §a" + ChatListener.getCooldown() + "s Cooldown");
            return true;
        } else if (args.length == 1 && !args[0].equalsIgnoreCase("help")) {
            if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("off")) {
                ChatListener.setCooldown(0);
                Msg.send(sender, "§2Du hast den Slowchat-Modus deaktiviert!");

                return true;
            } else if (args[0].equalsIgnoreCase("on")) {
                ChatListener.setCooldown(DEFAULT_COOLDOWN);
                Msg.send(sender, "§2Der Slowchat-Modus wurde auf §a" + DEFAULT_COOLDOWN + "s§2 gesetzt!");

                return true;
            } else {
                try {
                    int cooldown = Integer.parseInt(args[0]);
                    ChatListener.setCooldown(cooldown);
                    Msg.send(sender, "§2Der Slowchat-Modus wurde auf §a" + cooldown + "s§2 gesetzt!");

                    return true;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        Msg.send(sender, "§4Bitte benutze: §c/slowchat [<on|off|seconds>]");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"on", "off"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }
}

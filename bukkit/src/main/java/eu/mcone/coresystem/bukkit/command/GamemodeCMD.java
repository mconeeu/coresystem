/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCMD extends CoreCommand {

    public GamemodeCMD() {
        super("gamemode", "system.bukkit.gamemode", "gm");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                for (GameMode gamemode : GameMode.values()) {
                    if (args[0].equalsIgnoreCase(gamemode.name()) || args[0].equalsIgnoreCase(String.valueOf(gamemode.getValue()))) {
                        setGamemode(p, gamemode);
                        return true;
                    }
                }

                Msg.send(p, "§4Bitte benutze: §c/gm §4oder §c/gamemode <Gamemode>");
                Sound.error(p);
                return false;
            } else {
                Msg.sendTransl(sender, "system.command.consolesender");
            }
        } else if (args.length == 2) {
            Player t = Bukkit.getPlayer(args[1]);

            if (t != null) {
                for (GameMode gamemode : GameMode.values()) {
                    if (args[0].equalsIgnoreCase(gamemode.name()) || args[0].equalsIgnoreCase(String.valueOf(gamemode.getValue()))) {
                        setGamemode(t, gamemode);
                        Msg.send(sender, "§2Du hast den Spielmodus von §f" + t.getName() + " §2auf §a" + t.getGameMode() + "§2 gesetzt.");
                        return true;
                    }
                }
            }
        } else {
            Msg.send(sender, "§4Bitte benutze: §c/gm §4oder §c/gamemode <0|1|2|3> [<player>]");
        }

        return true;
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

        for (GameMode gamemode : GameMode.values()) {
            if (gamemode.name().toLowerCase().startsWith(search)) {
                matches.add(gamemode.name().toLowerCase());
            }
        }

        return matches;
    }

    private void setGamemode(Player p, GameMode gamemode) {
        p.setGameMode(gamemode);
        p.setAllowFlight(gamemode.equals(GameMode.CREATIVE) || gamemode.equals(GameMode.SPECTATOR));

        Sound.change(p);
        Msg.send(p, "§2Du hast deinen Spielmodus auf §f" + p.getGameMode() + " §2gesetzt!");
    }

}
/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GamemodeCMD extends CorePlayerCommand {

    public GamemodeCMD() {
        super("gamemode", "system.bukkit.gamemode", "gm");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("0")) {
                p.setGameMode(GameMode.SURVIVAL);
                p.setAllowFlight(false);
            } else if (args[0].equalsIgnoreCase("1")) {
                p.setGameMode(GameMode.CREATIVE);
                p.setAllowFlight(true);
            } else if (args[0].equalsIgnoreCase("2")) {
                p.setGameMode(GameMode.ADVENTURE);
                p.setAllowFlight(false);
            } else if (args[0].equalsIgnoreCase("3")) {
                p.setGameMode(GameMode.SPECTATOR);
                p.setAllowFlight(true);
            } else {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/gm §4oder §c/gamemode <Gamemode>");
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                return true;
            }

            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast deinen Spielmodus auf §f" + p.getGameMode() + " §2gesetzt!");
            p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 1);
        } else if (args.length == 2) {
            try {
                Player t = Bukkit.getPlayer(args[1]);

                if (args[0].equalsIgnoreCase("1")) {
                    t.setGameMode(GameMode.CREATIVE);
                    t.setAllowFlight(true);
                } else if (args[0].equalsIgnoreCase("0")) {
                    t.setGameMode(GameMode.SURVIVAL);
                    t.setAllowFlight(false);
                } else if (args[0].equalsIgnoreCase("2")) {
                    t.setGameMode(GameMode.ADVENTURE);
                    t.setAllowFlight(false);
                } else if (args[0].equalsIgnoreCase("3")) {
                    t.setGameMode(GameMode.SPECTATOR);
                    t.setAllowFlight(true);
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/gm §4oder §c/gamemode <Gamemode> [<Spieler>]");
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    return true;
                }

                BukkitCoreSystem.getInstance().getMessager().send(t, "§7Dein Spielmodus wurde auf §f" + t.getGameMode() + " §7gesetzt.");
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast den Spielmodus von §f" + t.getName() + " §2auf §a" + t.getGameMode() + "§2 gesetzt.");
                t.playSound(t.getLocation(), Sound.BLAZE_HIT, 1, 1);
                p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 1);
            } catch (NullPointerException d) {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Dieser Spieler ist nicht Online oder existiert nicht!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/gm §4oder §c/gamemode <GamemodeCMD> [<Spieler>]");
        }

        return true;
    }
}
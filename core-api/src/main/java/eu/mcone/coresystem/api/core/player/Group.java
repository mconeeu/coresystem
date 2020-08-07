/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public enum Group {

    ADMIN(0, "Admin", "§c§oAdmin", "§8[§cA§8] §7", ChatColor.RED, 10),
    SRDEVELOPER(10, "SrDeveloper", "§b§oSrDeveloper", "§8[§bSrDev§8] §7", ChatColor.AQUA, 20),
    SRBUILDER(11, "SrBuilder", "§e§oSrBuilder", "§8[§eSrB§8] §7", ChatColor.YELLOW, 21),
    SRMODERATOR(12, "SrModerator", "§2§oSrModerator", "§8[§2SrMod§8] §7", ChatColor.DARK_GREEN, 22),
    SRCONTENT(13, "SrContent", "§2§oSrModerator", "§8[§2SrMod§8] §7", ChatColor.BLUE, 22),
    DEVELOPER(20, "Developer", "§b§oDeveloper", "§8[§bDev§8] §7", ChatColor.AQUA, 30),
    BUILDER(21, "Builder", "§e§oBuilder", "§8[§eB§8] §7", ChatColor.YELLOW, 32),
    MODERATOR(22, "Moderator", "§2§oModerator", "§8[§2Mod§8] §7", ChatColor.DARK_GREEN, 33),
    CONTENT(23, "Content", "§9§oContent", "§8[§9Con§8] §7", ChatColor.BLUE, 31),
    SUPPORTER(24, "Supporter", "§a§oSupporter", "§8[§aSup§8] §7", ChatColor.GREEN, 34),
    JRDEVELOPER(30, "Developer", "§b§oDeveloper", "§8[§bJrDev§8] §7", ChatColor.AQUA, 40),
    JRBUILDER(31, "JrBuilder", "§e§oJrBuilder", "§8[§eJrB§8] §7", ChatColor.YELLOW, 41),
    JRSUPPORTER(32, "JrSupporter", "§a§oJrSupporter", "§8[§aJrSup§8] §7", ChatColor.GREEN, 42),
    TEAM(40, "Team", "§f§oTeam", "§8[§fTeam§8] §7", ChatColor.WHITE, 1000),
    CREATOR(50, "Creator", "§5§oCreator", "§8[§5Cr§8] §7", ChatColor.DARK_PURPLE, 50),
    ONE(51, "One", "§3§oOne", "§8[§3ONE§8] §7", ChatColor.DARK_AQUA, 51),
    PREMIUMPLUS(60, "Premium+", "§6§oPremium+", "§8[§6P+§8] §7", ChatColor.GOLD, 60),
    PREMIUM(61, "Premium", "§6§oPremium", "§8[§6P§8] §7", ChatColor.GOLD, 61),
    SPIELER(70, "Spieler", "§f§oSpieler", "§8[§fS§8] §7", ChatColor.WHITE, 70),
    SPIELVERDERBER(71, "Spielverderber", "§8Spielverderber", "§8[§0SV§8] §8", ChatColor.DARK_GRAY, 71);

    private final int id, score;
    private final String name, label, prefix;
    private final ChatColor color;

    Group(int id, String name, String label, String prefix, ChatColor color, int score) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.prefix = prefix;
        this.color = color;
        this.score = score;
    }

    public boolean standsAbove(Group group) {
        return id < group.id;
    }

    public static Group getGroupbyName(String name) {
        for (Group group : values()) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }

    public static Group getGroupById(int id) {
        for (Group group : values()) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }

}

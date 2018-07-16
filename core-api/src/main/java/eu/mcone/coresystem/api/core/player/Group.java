/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;

public enum Group {

    ADMIN(0, "Admin", "§c§oAdmin", "§8[§cA§8] §7", 10, -1),
    DEVELOPER(1, "Developer", "§b§oDeveloper", "§8[§bDev§8] §7", 20, -1),
    BUILDER(2, "Builder", "§e§oBuilder", "§8[§eB§8] §7", 30, -1),
    SRMODERATOR(3, "SrModerator", "§2§oSrModerator", "§8[§2SrMod§8] §7", 35, -1),
    MODERATOR(4, "Moderator", "§2§oModerator", "§8[§2Mod§8] §7", 40, -1),
    SUPPORTER(5, "Supporter", "§a§oSupporter", "§8[§aSup§8] §7", 50, -1),
    JRSUPPORTER(6, "JrSupporter", "§a§oJrSupporter", "§8[§aJrSup§8] §7", 55, -1),
    TEAM(7, "Team", "§f§oTeam", "§8[§fTeam§8] §7", 1000, -1),
    YOUTUBER(8, "YouTuber", "§5§oYoutuber", "§8[§5YT§8] §7", 60, 20),
    PREMIUMPLUS(9, "Premium+", "§6§oPremium+", "§8[§6P+§8] §7", 70, 21),
    PREMIUM(10, "Premium", "§6§oPremium", "§8[§6P§8] §7", 80, 22),
    SPIELER(11, "Spieler", "§f§oSpieler", "§8[§fS§8] §7", 90, -1),
    SPIELVERDERBER(12, "Spielverderber", "§8Spielverderber", "§8[§0SV§8] §8", 99, 32);

    @Getter
    private int id, score, tsId;
    @Getter
    private String name, label, prefix;

    Group(int id, String name, String label, String prefix, int score, int tsId) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.prefix = prefix;
        this.score = score;
        this.tsId = tsId;
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

    public static Group getGroupByTsId(int tsId) {
        for (Group group : Group.values()) {
            if (group.getTsId() == tsId) {
                return group;
            }
        }
        return null;
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;

public enum Group {

    ADMIN(0, "Admin", "§c§oAdmin", "§8[§cA§8] §7", "§c", 10, -1),
    CONTENT(1, "Content", "§9§oContent", "§8[§9Con§8] §7", "§9", 20, -1),
    DEVELOPER(2, "Developer", "§b§oDeveloper", "§8[§bDev§8] §7", "§b", 30, -1),
    BUILDER(3, "Builder", "§e§oBuilder", "§8[§eB§8] §7", "§e", 40, -1),
    SRMODERATOR(4, "SrModerator", "§2§oSrModerator", "§8[§2SrMod§8] §7", "§2", 45, -1),
    MODERATOR(5, "Moderator", "§2§oModerator", "§8[§2Mod§8] §7", "§2", 50, -1),
    SUPPORTER(6, "Supporter", "§a§oSupporter", "§8[§aSup§8] §7", "§a", 60, -1),
    JRSUPPORTER(7, "JrSupporter", "§a§oJrSupporter", "§8[§aJrSup§8] §7", "§a", 65, -1),
    TEAM(8, "Team", "§f§oTeam", "§8[§fTeam§8] §7", "§f", 1000, -1),
    YOUTUBER(9, "YouTuber", "§5§oYoutuber", "§8[§5YT§8] §7", "§5", 70, 20),
    PREMIUMPLUS(10, "Premium+", "§6§oPremium+", "§8[§6P+§8] §7", "§6", 75, 21),
    PREMIUM(11, "Premium", "§6§oPremium", "§8[§6P§8] §7", "§6", 80, 22),
    SPIELER(12, "Spieler", "§f§oSpieler", "§8[§fS§8] §7", "§f", 90, -1),
    SPIELVERDERBER(13, "Spielverderber", "§8Spielverderber", "§8[§0SV§8] §8", "§8", 95, 32);

    @Getter
    private int id, score, tsId;
    @Getter
    private String name, label, prefix, formattingCode;

    Group(int id, String name, String label, String prefix, String formattingCode, int score, int tsId) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.prefix = prefix;
        this.formattingCode = formattingCode;
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

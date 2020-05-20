/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;

public enum Group {

    ADMIN(0, "Admin", "§c§oAdmin", "§8[§cA§8] §7", "§c", 10, -1),
    CONTENT(1, "Content", "§9§oContent", "§8[§9Con§8] §7", "§9", 20, -1),
    SRDEVELOPER(2, "SrDeveloper", "§b§oSrDeveloper", "§8[§bSrDev§8] §7", "§b", 29, -1),
    DEVELOPER(3, "Developer", "§b§oDeveloper", "§8[§bDev§8] §7", "§b", 30, -1),
    JRDEVELOPER(4, "Developer", "§b§oDeveloper", "§8[§bJrDev§8] §7", "§b", 31, -1),
    SRBUILDER(5, "SrBuilder", "§e§oSrBuilder", "§8[§eSrB§8] §7", "§e", 39, -1),
    BUILDER(6, "Builder", "§e§oBuilder", "§8[§eB§8] §7", "§e", 40, -1),
    JRBUILDER(7, "JrBuilder", "§e§oJrBuilder", "§8[§eJrB§8] §7", "§e", 41, -1),
    SRMODERATOR(8, "SrModerator", "§2§oSrModerator", "§8[§2SrMod§8] §7", "§2", 49, -1),
    MODERATOR(9, "Moderator", "§2§oModerator", "§8[§2Mod§8] §7", "§2", 50, -1),
    SUPPORTER(10, "Supporter", "§a§oSupporter", "§8[§aSup§8] §7", "§a", 60, -1),
    JRSUPPORTER(11, "JrSupporter", "§a§oJrSupporter", "§8[§aJrSup§8] §7", "§a", 61, -1),
    TEAM(12, "Team", "§f§oTeam", "§8[§fTeam§8] §7", "§f", 1000, -1),
    CREATOR(13, "Creator", "§5§oCreator", "§8[§5Cr§8] §7", "§5", 70, 20),
    ONE(14, "One", "§3§oOne", "§8[§3ONE§8] §7", "§3", 71, -1),
    PREMIUMPLUS(15, "Premium+", "§6§oPremium+", "§8[§6P+§8] §7", "§6", 79, 21),
    PREMIUM(16, "Premium", "§6§oPremium", "§8[§6P§8] §7", "§6", 80, 22),
    SPIELER(17, "Spieler", "§f§oSpieler", "§8[§fS§8] §7", "§f", 90, -1),
    SPIELVERDERBER(18, "Spielverderber", "§8Spielverderber", "§8[§0SV§8] §8", "§8", 91, 32);

    @Getter
    private final int id, score, tsId;
    @Getter
    private final String name, label, prefix, formattingCode;

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

/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public enum Group {

    ADMIN(0, "Admin", "§c§oAdmin", "§8[§cA§8] §7", 10),
    DEVELOPER(1, "Developer", "§b§oDeveloper", "§8[§bDev§8] §7", 20),
    BUILDER(2, "Builder", "§e§oBuilder", "§8[§eB§8] §7", 30),
    SRMODERATOR(3, "SrModerator", "§2§oSrModerator", "§8[§2SrMod§8] §7", 35),
    MODERATOR(4, "Moderator", "§2§oModerator", "§8[§2Mod§8] §7", 40),
    SUPPORTER(5, "Supporter", "§a§oSupporter", "§8[§aSup§8] §7", 50),
    JRSUPPORTER(6, "JrSupporter", "§a§oJrSupporter", "§8[§aJrSup§8] §7", 55),
    TEAM(7, "Team", "f§oTeam", "§8[§fTeam§8] §7", 1000),
    YOUTUBER(8, "YouTuber", "§5§oYoutuber", "§8[§5YT§8] §7", 60),
    PREMIUMPLUS(9, "Premium+", "§6§oPremium+", "§8[§6P+§8] §7", 70),
    PREMIUM(10, "Premium", "§6§oPremium", "§8[§6P§8] §7", 80),
    SPIELER(11, "Spieler", "§f§oSpieler", "§8[§fS§8] §7", 90),
    SPIELVERDERBER(12, "Spielverderber", "§8§oSpielverderber", "§8[SV] §8", 100);

    @Getter
    private int id, score;
    @Getter
    private String name, label, prefix;

    Group(int id, String name, String label, String prefix, int score) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.prefix = prefix;
        this.score = score;
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

    public static Set<Group> getGroups(String json) {
        Set<Group> groups = new HashSet<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();

        for (JsonElement e : array) {
            groups.add(Group.getGroupById(e.getAsInt()));
        }

        return groups;
    }

    public static String getJson(Set<Group> groups) {
        JsonArray array = new JsonArray();
        for (Group group : groups) array.add(group.getId());

        return array.getAsString();
    }

}

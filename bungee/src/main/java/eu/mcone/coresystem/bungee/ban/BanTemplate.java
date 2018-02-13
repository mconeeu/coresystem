/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.ban;

public enum BanTemplate {

    CLIENTMODS("CM", "Clientmods", 10, 0),
    ERSCHEINEN("ES", "Erscheinen", 1, 0),
    TEAMTROLLING("TT", "TeamTrolling", 2, 0),
    BETRUG("BT", "Betrug", 10, 0),
    BUGUSING("BU", "Bugusing", 2, 0),
    RADIKALISMUS("RM", "Radikalismus", 10, 20),
    BELEIDIGUNG("BL", "Beleidigung", 0, 2),
    DROHUNG("DH", "Drohung", 5, 5),
    SPAM("SP", "Spam", 0, 2),
    WERBUNG("WB", "Werbung", 0, 2);

    private int banPoints, mutePoints;
    private String id, name;

    BanTemplate(String id, String name, int banPoints, int mutePoints) {
        this.id = id;
        this.name = name;
        this.banPoints = banPoints;
        this.mutePoints = mutePoints;
    }

    public static BanTemplate getTemplateByID(String id) {
        for (BanTemplate r : values()) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public int getBanPoints() {
        return banPoints;
    }

    public int getMutePoints() {
        return mutePoints;
    }

    public String getName() {
        return name;
    }
}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils.bots.discord.command.utils;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;

public enum Role {

    EVERYONE(12, "everyone", 500734222680588288L),
    VERIFIED(11, "Verifiziert", 500734222680588288L),
    FREUND(10, "Freund", 500734222680588288L),
    SPIELER(9, "Spieler", 500734222680588288L),
    PREMIUM(8, "Premium", 500734222680588288L),
    PREMIUMPLUS(7, "Premium+", 500734459893776394L),
    YOUTUBER(6, "YouTuber", 500596103780106250L),
    SUPPORTER(5, "Supporter", 500596103780106250L),
    MODERATOR(4, "Moderator", 500596103780106250L),
    BUILDER(3, "Builder", 500596103780106250L),
    DEVELOPER(2, "Developer", 500594807765336065L),
    ADMIN(1, "Admin", 500596103780106250L);

    private final int hierarchyID;
    private final String name;
    private final long ID;

    Role(final int hierarchyID, final String name, final long ID) {
        this.hierarchyID = hierarchyID;
        this.name = name;
        this.ID = ID;
    }

    public int getHierarchyID() {
        return hierarchyID;
    }

    public String getName() {
        return name;
    }

    public long getID() {
        return ID;
    }

    public Role getRoleByHierarchyID(final int hierarchyID) {
        for (Role discordRoles : Role.values()) {
            if (hierarchyID >= 12 || hierarchyID <= 0) {
                BungeeCoreSystem.getInstance().sendConsoleMessage("§cHierarchyID must be smaller then 12 and bigger then 0.");
            } else {
                if (discordRoles.getHierarchyID() == hierarchyID) {
                    return discordRoles;
                }
            }
        }
        return Role.EVERYONE;
    }
}

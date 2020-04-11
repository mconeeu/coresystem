/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.gamemode;


import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Gamemode {

    BEDWARS("BW", "Bedwars", ChatColor.RED, Material.BED),
    SKYPVP("SP", "SkyPvP", ChatColor.BLUE, Material.DIAMOND_SWORD),
    KNOCKIT("KI", "KnockIT", ChatColor.DARK_GREEN, Material.STICK),
    MINEWAR("MW", "Minewar", ChatColor.LIGHT_PURPLE, Material.COBBLESTONE),
    TRASHWARS("TW", "Trashwars", ChatColor.GREEN, Material.IRON_PICKAXE),
    BUILD("BU", "Build", ChatColor.YELLOW, Material.GRASS),
    UNDEFINED("UD", "undefined", ChatColor.DARK_GRAY, Material.COAL);

    @Getter
    private String id, name;
    @Getter
    private ChatColor color;
    @Getter
    private Material item;

    Gamemode(String id, String name, ChatColor color, Material item) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.item = item;
    }

    public String getLabel() {
        return ChatColor.BOLD.toString() + color.toString() + name;
    }

}

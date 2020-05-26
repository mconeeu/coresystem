/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicInventory {

    private long lastUpdate;
    private Gamemode gamemode;
    private String category;
    private String name;
    private String title;
    private int size;

}

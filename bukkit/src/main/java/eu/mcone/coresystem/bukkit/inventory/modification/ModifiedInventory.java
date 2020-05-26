/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class ModifiedInventory extends BasicInventory {

    private Map<String, UUID> uniqueItemStacks;

    public ModifiedInventory(long lastUpdate, final Gamemode gamemode, final String category, final String name, final String title, final int size, final Map<String, UUID> uniqueItemStacks) {
        super(lastUpdate, gamemode, category, name, title, size);
        this.uniqueItemStacks = uniqueItemStacks;
    }

    public ModifiedInventory(final Document document) {
        this(
                document.getLong("lastUpdate"),
                Gamemode.valueOf(document.getString("gamemode")),
                document.getString("category"),
                document.getString("name"),
                document.getString("title"),
                document.getInteger("size"),
                (Map<String, UUID>) document.get("uniqueItemStacks")
        );
    }

}

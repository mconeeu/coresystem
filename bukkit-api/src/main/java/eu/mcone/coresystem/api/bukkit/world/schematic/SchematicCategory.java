package eu.mcone.coresystem.api.bukkit.world.schematic;

import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;

import java.util.UUID;

public interface SchematicCategory {

    String getId();

    String getName();

    String getMaterial();

    UUID getCreator();

    void setName(String name);

    ItemBuilder getItem();

}

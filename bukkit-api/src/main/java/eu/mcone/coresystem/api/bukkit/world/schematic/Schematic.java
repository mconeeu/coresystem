package eu.mcone.coresystem.api.bukkit.world.schematic;

import java.util.UUID;

public interface Schematic {

    String getId();

    String getName();

    UUID getAuthor();

    byte[] getSchematicData();

    long getCreated();

    void addCategories(String... categories);

    void removeCategory(String id);

    void addCategories(SchematicCategory... categories);

}

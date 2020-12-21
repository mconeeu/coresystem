package eu.mcone.coresystem.api.bukkit.world.schematic;

import org.bukkit.Material;

import java.util.UUID;

public interface SchematicManager {

    void clearCache();

    boolean existsSchematic(String name);

    Schematic insertSchematic(Schematic schematic);

    Schematic insertSchematic(String name, UUID author, String path);

    SchematicCategory insertCategory(String name, Material material, UUID creator);

    short replaceSchematic(Schematic schematic);

    short replaceCategory(SchematicCategory category);

    Schematic getSchematic(String name);

    String getCategoryID(String name);

    SchematicCategory getCategoryByName(String name);

    SchematicCategory getCategoryByID(String id);

    short deleteSchematic(String name);

    short deleteCategoryByName(String name);

    short deleteCategoryById(String id);

    long countSchematics();
}

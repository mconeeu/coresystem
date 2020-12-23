package eu.mcone.coresystem.api.bukkit.world.schematic;

import com.mongodb.client.FindIterable;
import org.bukkit.Material;

import java.util.UUID;

public interface SchematicManager {

    void clearCache();

    boolean existsSchematic(String name);

    eu.mcone.coresystem.api.bukkit.world.schematic.Schematic createSchematic(String name, UUID author);

    eu.mcone.coresystem.api.bukkit.world.schematic.Schematic createSchematic(String name, UUID author, String... categories);

    Schematic createSchematic(Schematic schematic);

    SchematicCategory createCategory(String name, Material material, UUID creator);

    short replaceSchematic(Schematic schematic);

    short replaceCategory(SchematicCategory category);

    FindIterable<Schematic> getSchematics(int skip, int limit, String... projections);

    FindIterable<Schematic> getSchematics(int skip, int limit, String[] projections, String[] categories);

    Schematic getSchematic(String name);

    Schematic getSchematicById(String id);

    FindIterable<SchematicCategory> getCategories(int skip, int limit);

    String getCategoryID(String name);

    SchematicCategory getCategory(String name);

    SchematicCategory getCategoryByID(String id);

    short deleteSchematic(String name, String path);

    short deleteSchematicById(String id);

    short deleteCategory(String name);

    short deleteCategoryById(String id);

    long countSchematics();

    long countSchematics(String... categories);
}

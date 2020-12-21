package eu.mcone.coresystem.bukkit.world.schematic;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bukkit.Material;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class SchematicManager implements eu.mcone.coresystem.api.bukkit.world.schematic.SchematicManager {

    public static final String SCHEMATIC_COLLECTION = "schematics";
    public static final String SCHEMATIC_CATEGORIES_COLLECTION = "schematic_categories";

    @Getter
    private static SchematicManager instance;

    @Getter
    private final boolean cache;

    private final MongoCollection<eu.mcone.coresystem.api.bukkit.world.schematic.Schematic> schematicCollection;
    private final MongoCollection<eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory> schematicCategoryCollection;

    private HashMap<String, eu.mcone.coresystem.api.bukkit.world.schematic.Schematic> cachedSchematics;

    public SchematicManager(boolean cache) {
        instance = this;
        this.cache = cache;

        MongoDatabase cloudDatabase = ((CoreModuleCoreSystem) instance).getMongoDB(Database.CLOUD);
        schematicCollection = cloudDatabase.getCollection(SCHEMATIC_COLLECTION, eu.mcone.coresystem.api.bukkit.world.schematic.Schematic.class);
        schematicCategoryCollection = cloudDatabase.getCollection(SCHEMATIC_CATEGORIES_COLLECTION, eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory.class);

        if (cache) {
            cachedSchematics = new HashMap<>();
        }
    }

    /**
     * Clears the local cache
     */
    public void clearCache() {
        if (cache) {
            cachedSchematics.clear();
        }
    }

    /**
     * Checks if the Schematic with the given name exists in the database
     *
     * @param name Schematic name
     * @return boolean
     */
    public boolean existsSchematic(String name) {
        if (cache) {
            if (cachedSchematics.containsKey(name)) {
                return true;
            }
        }

        return schematicCollection.find(eq("name", name)).first() != null;
    }

    /**
     * Inserts a new Schematic in the database
     *
     * @param schematic CloudSchematic
     * @throws KeyAlreadyExistsException If a schematic with the same id already exists in the database.
     */
    public eu.mcone.coresystem.api.bukkit.world.schematic.Schematic insertSchematic(eu.mcone.coresystem.api.bukkit.world.schematic.Schematic schematic) {
        if (!existsSchematic(schematic.getName())) {
            schematicCollection.insertOne(schematic);
        } else {
            throw new KeyAlreadyExistsException("The name " + schematic.getName() + " already exists in the database!");
        }

        if (cache) {
            cachedSchematics.put(schematic.getId(), schematic);
        }

        return schematic;
    }

    /**
     * Inserts a new Schematic in the database
     *
     * @param name   Schematic name
     * @param author The one who created the schematic
     * @param path   The path to the schematic file
     * @throws KeyAlreadyExistsException If a schematic with the same id already exists in the database.
     */
    public eu.mcone.coresystem.api.bukkit.world.schematic.Schematic insertSchematic(String name, UUID author, String path) {
        eu.mcone.coresystem.bukkit.world.schematic.Schematic cloudSchematic = new eu.mcone.coresystem.bukkit.world.schematic.Schematic(name, author);
        cloudSchematic.upload(path);
        return cloudSchematic;
    }

    /**
     * creates and inserts a new schematic category
     *
     * @param name     Category name
     * @param material Material that represents the category
     * @param creator  The creator of the category
     * @return SchematicCategory object
     * @throws KeyAlreadyExistsException If a category with the same name already exists in the database
     */
    public SchematicCategory insertCategory(String name, Material material, UUID creator) {
        if (schematicCategoryCollection.find(eq("name", name)).first() == null) {
            SchematicCategory category = new SchematicCategory(name, material, creator);
            schematicCategoryCollection.insertOne(category);
            return category;
        } else {
            throw new KeyAlreadyExistsException("A category with the name " + name + " already exists!");
        }
    }

    /**
     * Replaces the given schematic in the database
     *
     * @param schematic Schematic object
     * @return modified entries
     */
    public short replaceSchematic(eu.mcone.coresystem.api.bukkit.world.schematic.Schematic schematic) {
        if (cache) {
            this.cachedSchematics.put(schematic.getId(), schematic);
        }

        return (short) schematicCollection.replaceOne(eq("_id", schematic.getId()), schematic).getModifiedCount();
    }

    /**
     * Replaces the given category in the database
     *
     * @param category SchematicCategory object
     * @return modified entries
     */
    public short replaceCategory(eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory category) {
        return (short) schematicCategoryCollection.replaceOne(eq("_id", category.getId()), category).getModifiedCount();
    }

    /**
     * Returns the Schematic for the given id
     *
     * @param name Schematic name
     * @return CloudSchematic
     * @throws NullPointerException If the Schematic with the given id doesn't exists in the database
     */
    public eu.mcone.coresystem.api.bukkit.world.schematic.Schematic getSchematic(String name) {
        if (cache) {
            if (cachedSchematics.containsKey(name)) {
                return cachedSchematics.get(name);
            }
        }

        eu.mcone.coresystem.api.bukkit.world.schematic.Schematic cloudSchematic = schematicCollection.find(eq("name", name)).first();

        if (cloudSchematic != null) {
            if (cache) {
                cachedSchematics.put(cloudSchematic.getId(), cloudSchematic);
            }

            return cloudSchematic;
        } else {
            throw new NullPointerException("Could not find schematic with the name " + name);
        }
    }

    /**
     * Returns the ID for the category with the passed name
     *
     * @param name Category name
     * @return Category id
     */
    public String getCategoryID(String name) {
        eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory category = schematicCategoryCollection.find(eq("name", name)).projection(Projections.fields(Projections.include("_id"))).first();

        if (category != null) {
            return category.getId();
        } else {
            return null;
        }
    }

    /**
     * Returns a SchematicCategory object for the given name
     *
     * @param name Category name
     * @return SchematicCategory
     */
    public eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory getCategoryByName(String name) {
        return schematicCategoryCollection.find(eq("name", name)).first();
    }

    /**
     * Returns a SchematicCategory object for the given id
     *
     * @param id Category id
     * @return SchematicCategory
     */
    public eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory getCategoryByID(String id) {
        return schematicCategoryCollection.find(eq("_id", id)).first();
    }

    /**
     * Deletes a Schematic for the given id
     *
     * @param name Schematic name
     */
    public short deleteSchematic(String name) {
        eu.mcone.coresystem.api.bukkit.world.schematic.Schematic schematic = getSchematic(name);

        if (schematic != null) {
            if (cache) {
                cachedSchematics.remove(schematic.getId());
            }

            schematicCollection.deleteMany(eq("_id", schematic.getId()));
            return 1;
        }

        return 0;
    }

    /**
     * Deletes the category with the passed name
     *
     * @param name Category name
     * @return deleted documents
     */
    public short deleteCategoryByName(String name) {
        String id = getCategoryID(name);

        if (id != null) {
            removeCategory(id);

            return (short) schematicCategoryCollection.deleteMany(eq("name", name)).getDeletedCount();
        } else {
            return 0;
        }
    }

    /**
     * Deletes the category with the passed id
     *
     * @param id Category id
     * @return deleted documents
     */
    public short deleteCategoryById(String id) {
        removeCategory(id);
        return (short) schematicCategoryCollection.deleteMany(eq("_id", id)).getDeletedCount();
    }

    private void removeCategory(String categoryID) {
        for (eu.mcone.coresystem.api.bukkit.world.schematic.Schematic schematic : schematicCollection.find(in("categories", categoryID))) {
            schematic.removeCategory(categoryID);
            replaceSchematic(schematic);
        }
    }

    /**
     * Counts all available Schematics in the database
     *
     * @return long
     */
    public long countSchematics() {
        return schematicCollection.countDocuments();
    }
}

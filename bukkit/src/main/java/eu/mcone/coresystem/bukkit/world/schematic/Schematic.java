package eu.mcone.coresystem.bukkit.world.schematic;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class Schematic implements eu.mcone.coresystem.api.bukkit.world.schematic.Schematic {

    @BsonProperty("_id")
    private String id;

    private String name;
    private UUID author;
    private HashSet<String> categories;
    private byte[] schematicData;
    private long created;

    public Schematic(String name, UUID author) {
        this.id = CoreSystem.getInstance().getUniqueIdUtil().getUniqueKey("schematic");
        this.name = name;
        this.author = author;
        this.categories = new HashSet<>();
        this.created = System.currentTimeMillis() / 1000;
    }

    public Schematic(String name, UUID author, String... categories) {
        this(name, author);
        this.categories = new HashSet<>(Arrays.asList(categories));
    }

    /**
     * Fetches the content from the Schematic file and creates a database entry.
     *
     * @param path The path of the schematic file to be transferred
     * @throws NullPointerException      If the file doesn't exists.
     * @throws KeyAlreadyExistsException If a schematic with the same id already exists in the database.
     */
    public void upload(String path) {
        try {
            if (!SchematicManager.getInstance().existsSchematic(name)) {
                File file = new File(path);

                if (file.exists()) {
                    this.schematicData = FileUtils.readFileToByteArray(file);
                    SchematicManager.getInstance().insertSchematic(this);
                } else {
                    throw new NullPointerException("Could not find file " + path);
                }
            } else {
                throw new KeyAlreadyExistsException("The id " + name + " already exists in the database!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the schematic file
     *
     * @param path     The path where the schematic gets saved
     * @param override If the schematic already exists, it can be overwritten with this option.
     */
    public void create(String path, boolean override) {
        try {
            File file = new File(path, name);

            if (file.exists()) {
                if (override) {
                    FileUtils.writeByteArrayToFile(file, schematicData);
                }
            } else {
                FileUtils.writeByteArrayToFile(file, schematicData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the passed categories to the schematic
     *
     * @param categories Category names
     */
    public void addCategories(String... categories) {
        for (String category : categories) {
            String id = SchematicManager.getInstance().getCategoryID(category);
            if (id != null) {
                this.categories.add(id);
            }
        }
    }

    /**
     * Removes the Category with the given id
     *
     * @param id Category id
     */
    public void removeCategory(String id) {
        this.categories.remove(id);
    }

    /**
     * Adds the passed categories to the schematic
     *
     * @param categories SchematicCategory
     */
    public void addCategories(eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory... categories) {
        for (eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory category : categories) {
            this.categories.add(category.getId());
        }
    }

    @Override
    public String toString() {
        return "CloudSchematic{" +
                "id='" + name + '\'' +
                ", schematic=" + Arrays.toString(schematicData) +
                ", author=" + author +
                ", created=" + created +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schematic that = (Schematic) o;
        return created == that.created &&
                Objects.equals(name, that.name) &&
                Arrays.equals(schematicData, that.schematicData) &&
                Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, created, author, schematicData);
    }

}

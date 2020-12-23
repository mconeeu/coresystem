package eu.mcone.coresystem.api.bukkit.world.schematic;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface Schematic {

    String getId();

    String getName();

    UUID getAuthor();

    Set<String> getCategories();

    byte[] getSchematicData();

    long getCreated();

    void upload(String path, Consumer<Boolean> succeeded);

    void create(String path, boolean override);

    void addCategories(String... categories);

    void removeCategory(String id);

    void addCategories(SchematicCategory... categories);

}

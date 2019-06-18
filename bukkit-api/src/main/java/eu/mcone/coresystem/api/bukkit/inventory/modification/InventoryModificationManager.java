package eu.mcone.coresystem.api.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public interface InventoryModificationManager {

    Gamemode getGamemode();

    /**
     * Clears all Maps and Lists and pushes all modifications
     */
    void disable();

    Map<String, Map<String, ModifyInventory>> getMultipleInventories();

    /**
     * Loads all default inventories from the database
     */
    void loadDefaultInventories();

    /**
     * Loads all default inventories from the database where the specified gamemode
     *
     * @param gamemode Gamemode Enum
     */
    void loadDefaultInventories(final Gamemode gamemode);

    /**
     * Loads all modified inventories in the mcone data database
     * (very performance-heavy with many datasets in the database, use only if necessary)
     */
    void loadModifiedInventories();

    /**
     * Loads all modified inventories for the specified uuid
     *
     * @param player Bukkit Player
     * @return List of ModifiedInventories
     */
    Map<String, ModifiedInventory> loadModifiedInventories(final Player player);

    /**
     * Registers a new ModifyInventory object
     *
     * @param modifyInventory ModifyInventory
     */
    void registerInventory(final ModifyInventory modifyInventory);

    /**
     * Registers a List of ModifyInventory objects
     *
     * @param modifyInventory ModifyInventory
     */
    void registerInventories(final ModifyInventory... modifyInventory);

    /**
     * Checks if the Inventory with the specified name belongs to an category with more inventories.
     *
     * @param clazz Inventory name
     * @return boolean
     */
    boolean hasMultipleInventories(final Class<? extends ModifyInventory> clazz);

    /**
     * Modifies a specified inventory for the specified uuid
     *
     * @param player          Bukkit Player
     * @param modifyInventory Bukkit Inventory
     */
    boolean modifyInventory(final Player player, final ModifyInventory modifyInventory, final Inventory modifiedInventory);

    /**
     * Converts the items from default inventory to an Map
     *
     * @param defaultInventory target default inventory
     * @param inventory        inventory
     * @return Map
     */
    Map<String, UUID> getUniqueItems(DefaultInventory defaultInventory, Inventory inventory);

    /**
     * Pushes the modifications for all local stored players
     */
    void pushModifications();

    /**
     * Pushes the modifications for the specified player
     *
     * @param player Bukkit Player
     */
    void pushModifications(final Player player);

    /**
     * Checks if the inventory for the Player uuid and name is modified
     *
     * @param uuid         Player uniqueID
     * @param inventoryKey The uniqueKey of the class {gamemode.category.name}
     * @return boolean
     */
    boolean isInventoryModified(final UUID uuid, final String inventoryKey);

    /**
     * Returns all registered ModifyInventories
     *
     * @return List of GameInventories
     */
    List<ModifyInventory> getInventories();

    /**
     * Returns a list of modifyInventories with out an category
     *
     * @return list of modify inventory
     */
    List<ModifyInventory> getInventoriesWithOutCategory();

    /**
     * Returns the GameInventory where the specified name
     *
     * @param clazz Inventory name
     * @return GameInventory object
     */
    ModifyInventory getInventory(final Class<? extends ModifyInventory> clazz);

    /**
     * Returns a list of ModifyInventory with the same Gamemode
     *
     * @param gamemode Gamemode Enum
     * @return List of ModifyInventory with the same gamemode
     */
    List<ModifyInventory> getInventories(final Gamemode gamemode);

    /**
     * Returns all modified inventories for the specified uuid
     *
     * @param uuid Player uniqueID
     * @return Map of Strings and ModifiedInventories
     */
    Map<String, ModifiedInventory> getModifiedInventories(final UUID uuid);

    /**
     * Returns the modified Inventory for the specified uuid and inventory name
     *
     * @param player          Bukkit Player
     * @param modifyInventory ModifyInventory instance
     * @return ModifiedInventory
     */
    ModifiedInventory getModifiedInventory(final Player player, final ModifyInventory modifyInventory);

    /**
     * Returns a list of ModifiedInventories with the same category
     *
     * @param player   Bukkit Player
     * @param category Category of the inventory
     * @return List of ModifiedInventories with the same catgeory
     */
    List<ModifiedInventory> getModifiedInventories(final Player player, final String category);

    /**
     * Returns a list of modified inventories with the same gamemode
     *
     * @param player   Bukkit Player
     * @param gamemode Gamemode Enum
     * @return List of ModifiedInventories with the smae gamemode
     */
    List<ModifiedInventory> getModifiedInventories(final Player player, final Gamemode gamemode);

    /**
     * Returns a default inventory with the specified name
     *
     * @param inventoryKey UniqueKey of the inventory
     * @return DefaultInventory object
     */
    DefaultInventory getDefaultInventory(final String inventoryKey);

    /**
     * Returns all list of all default inventories in the database
     *
     * @return List of DefaultInventories
     */
    List<DefaultInventory> getDefaultInventories();

    /**
     * Returns a list of DefaultInventories with the same category
     *
     * @param category Category of the Inventory
     * @return List of DefaultInventories with the same category
     */
    List<DefaultInventory> getDefaultInventories(final String category);

    /**
     * Returns a list of DefaultInventories with the same gamemode
     *
     * @param gamemode Gamemode Enum
     * @return List of DefaultInventories with the same gamemode
     */
    List<DefaultInventory> getDefaultInventories(final Gamemode gamemode);

    /**
     * Returns a list of ModifyInventories were the specified category
     *
     * @param category Category
     * @return List of ModifyInventories with the same category
     */
    List<ModifyInventory> getMultipleInventoriesWhereCategory(final String category);

    /**
     * Returns modifyInventory inventories with the same gamemmode
     *
     * @param gamemode Bukkit Player
     * @return Map with ModifyInventory
     */
    Map<String, Map<String, ModifyInventory>> getMultipleInventoriesWhereGamemode(final Gamemode gamemode);

    /**
     * Returns a list of Game Inventories were the specified inventory name
     *
     * @param clazz Inventory name
     * @return List of ModifyInventories with the same category
     */
    List<ModifyInventory> getInventoriesWithCategory(final Class<? extends ModifyInventory> clazz);

    /**
     * Returns a map of all modify inventories with an category
     *
     * @return Map<String, Map<String, ModifyInventory>>
     */
    Map<String, Map<String, ModifyInventory>> getInventoriesWithCategories();

    /**
     * Sets for the specified player an modified inventory
     *
     * @param player          Bukkit Player
     * @param modifyInventory Currently Modifying
     * @return ModifyInventory
     */
    ModifyInventory addCurrentlyModifying(Player player, ModifyInventory modifyInventory);

    /**
     * Check if the player modified currently
     *
     * @param player Bukkit Player
     * @return boolean
     */
    boolean isCurrentlyModifying(Player player);

    /**
     * Returns the currently modifying inventory for the player
     *
     * @param player Bukkit Player
     * @return Current ModifyInventory
     */
    ModifyInventory getCurrentlyModifying(Player player);

    /**
     * Returns the last modification date for the modified inventory
     *
     * @param player Bukkit Player
     * @param clazz  Inventory name
     * @return Date string
     */
    String getLastUpdate(final Player player, final Class<? extends ModifyInventory> clazz);


    /**
     * Creates the ModificationInventory for the player
     *
     * @param player Bukkit Player
     */
    void createModificationInventory(Player player);


    /**
     * Parse the Map to an byte array
     *
     * @param defaultItems HashMap
     * @return Byte array
     */
    static byte[] toByteArray(Map<Integer, ModifyInventory.UniqueItemStack> defaultItems) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            //Convert ItemStack
            dataOutput.writeInt(defaultItems.size());

            for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> entry : defaultItems.entrySet()) {
                dataOutput.writeInt(entry.getKey());
                dataOutput.writeObject(entry.getValue());
            }

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Coverts a byte array to an hashmap object
     *
     * @param data byte array
     * @return HashMap
     */
    static Map<Integer, ModifyInventory.UniqueItemStack> fromByteArray(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Map<Integer, ModifyInventory.UniqueItemStack> defaultItems = new HashMap<>();

            int size = dataInput.readInt();
            for (int i = 0; i < size; i++) {
                defaultItems.put(dataInput.readInt(), (ModifyInventory.UniqueItemStack) dataInput.readObject());
            }

            return defaultItems;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}

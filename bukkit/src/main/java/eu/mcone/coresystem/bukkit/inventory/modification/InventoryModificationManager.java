package eu.mcone.coresystem.bukkit.inventory.modification;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modification.*;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class InventoryModificationManager implements eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager {

    @Getter
    private Gamemode gamemode;

    private HashMap<Class<? extends ModifyInventory>, ModifyInventory> modifyInventories;
    private HashMap<UUID, HashMap<String, ModifiedInventory>> modifiedInventories;
    private Map<String, DefaultInventory> defaultInventories;

    @Getter
    private TreeMap<String, Map<String, ModifyInventory>> multipleInventories;

    private Map<Player, ModifyInventory> currentlyModifing;

    private MongoCollection<Document> userInfoCollection;
    private MongoCollection<GenericDefaultInventory> defaultInventoriesCollection;

    public InventoryModificationManager(final Gamemode gamemode) {
        this.gamemode = gamemode;

        modifyInventories = new HashMap<>();
        modifiedInventories = new HashMap<>();

        defaultInventories = new HashMap<>();
        multipleInventories = new TreeMap<>(Comparator.naturalOrder());

        currentlyModifing = new HashMap<>();

        //Collection in the data database for all modified inventories
        userInfoCollection = BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo");
        defaultInventoriesCollection = BukkitCoreSystem.getSystem().getMongoDB(Database.DATA).getCollection("default_inventories", GenericDefaultInventory.class);

        //Load all default Inventories where the specified gamemode
        loadDefaultInventories(gamemode);
    }

    /**
     * Clears all Maps and Lists and pushes all modifications
     */
    public void disable() {
        pushModifications();

        modifyInventories.clear();
        defaultInventories.clear();
        modifiedInventories.clear();
        multipleInventories.clear();
        currentlyModifing.clear();
    }

    /**
     * Loads all default inventories from the database
     */
    public void loadDefaultInventories() {
        for (GenericDefaultInventory genericDefaultInventory : defaultInventoriesCollection.find()) {
            defaultInventories.put(getInventoryKey(genericDefaultInventory), genericDefaultInventory.parseToDefaultInventory());
        }
    }

    /**
     * Loads all default inventories from the database where the specified gamemode
     *
     * @param gamemode Gamemode Enum
     */
    public void loadDefaultInventories(final Gamemode gamemode) {
        if (gamemode.equals(Gamemode.UNDEFINED)) {
            for (GenericDefaultInventory genericDefaultInventory : defaultInventoriesCollection.find()) {
                defaultInventories.put(getInventoryKey(genericDefaultInventory), genericDefaultInventory.parseToDefaultInventory());
            }
        } else {
            for (GenericDefaultInventory genericDefaultInventory : defaultInventoriesCollection.find(eq("gamemode", gamemode.toString()))) {
                defaultInventories.put(getInventoryKey(genericDefaultInventory), genericDefaultInventory.parseToDefaultInventory());
            }
        }
    }

    /**
     * Loads all modified inventories in the mcone data database
     * (very performance-heavy with many datasets in the database, use only if necessary)
     */
    public void loadModifiedInventories() {
        for (Document inventories : userInfoCollection.find()) {
            if (inventories != null) {
                UUID playerUUID = UUID.fromString(inventories.getString("uuid"));
                Map<String, Document> dbModifiedInventories = (Map<String, Document>) inventories.get("modifiedInventories");

                if (!modifiedInventories.isEmpty()) {
                    for (Map.Entry<String, Document> entry : dbModifiedInventories.entrySet()) {
                        String inventoryKey = entry.getKey();
                        ModifiedInventory modifiedInventory = new ModifiedInventory(entry.getValue());

                        if (modifiedInventories.containsKey(playerUUID)) {
                            if (!modifiedInventories.get(playerUUID).containsKey(inventoryKey)) {
                                this.modifiedInventories.get(playerUUID).put(inventoryKey, modifiedInventory);
                            }
                        } else {
                            this.modifiedInventories.put(playerUUID, new HashMap<String, ModifiedInventory>() {{
                                put(inventoryKey, modifiedInventory);
                            }});
                        }
                    }

                    CoreSystem.getInstance().sendConsoleMessage("§aLoad ModifiedInventories for UUID: §7" + playerUUID + " §8(§7" + modifiedInventories.size() + " §7LOADED§8)");
                }
            }
        }
    }

    /**
     * Loads all modified inventories for the specified uuid
     *
     * @param player Bukkit Player
     * @return List of ModifiedInventories
     */
    public Map<String, ModifiedInventory> loadModifiedInventories(final Player player) {
        Document inventories = userInfoCollection.find(eq("uuid", player.getUniqueId().toString())).first();

        if (inventories != null) {
            Map<String, Document> dbModifiedInventories = (Map<String, Document>) inventories.get("modifiedInventories");

            if (dbModifiedInventories != null) {
                if (!dbModifiedInventories.isEmpty()) {
                    for (Map.Entry<String, Document> entry : dbModifiedInventories.entrySet()) {
                        String inventoryKey = entry.getKey();
                        ModifiedInventory modifiedInventory = new ModifiedInventory(entry.getValue());

                        if (modifiedInventories.containsKey(player.getUniqueId())) {
                            if (!modifiedInventories.get(player.getUniqueId()).containsKey(inventoryKey)) {
                                this.modifiedInventories.get(player.getUniqueId()).put(inventoryKey, modifiedInventory);
                            }
                        } else {
                            this.modifiedInventories.put(player.getUniqueId(), new HashMap<String, ModifiedInventory>() {{
                                put(inventoryKey, modifiedInventory);
                            }});

                        }
                    }

                    CoreSystem.getInstance().sendConsoleMessage("§aLoad ModifiedInventories for UUID: §7" + player.getUniqueId() + " §8(§7" + modifiedInventories.size() + " §7LOADED§8)");
                    return modifiedInventories.get(player.getUniqueId());
                }
            }
        } else {
            CoreSystem.getInstance().getMessager().send(player, "§cDeine Inventare konnten nicht geladen werden, §f§omelde dies bitte einem MCONE Teammitglied!");
        }

        return null;
    }

    /**
     * Registers a new ModifyInventory object
     *
     * @param modifyInventory ModifyInventory
     */
    public void registerInventory(final ModifyInventory modifyInventory) {
        modifyInventory.setGamemode(gamemode);
        modifyInventories.put(modifyInventory.getClass(), modifyInventory);

        if (modifyInventory.getCategory() != null) {
            if (multipleInventories.containsKey(modifyInventory.getCategory())) {
                multipleInventories.get(modifyInventory.getCategory()).put(modifyInventory.getInventoryKey(), modifyInventory);
            } else {
                multipleInventories.put(modifyInventory.getCategory(), new HashMap<String, ModifyInventory>() {{
                    put(modifyInventory.getInventoryKey(), modifyInventory);
                }});
            }
        }

        if (modifyInventory.getOptions().contains(CoreInventory.Option.CAN_MODIFY)) {
            DefaultInventory localDefaultInventory = new DefaultInventory(System.currentTimeMillis() / 1000,
                    gamemode.toString(),
                    modifyInventory.getCategory(),
                    modifyInventory.getName(),
                    modifyInventory.getTitle(),
                    modifyInventory.getSize(),
                    modifyInventory.getUniqueItemStacks()
            );

            GenericDefaultInventory dbGenericDefaultInventory = defaultInventoriesCollection.find(combine(
                    eq("gamemode", modifyInventory.getGamemode().toString()),
                    eq("category", modifyInventory.getCategory()),
                    eq("name", modifyInventory.getName())
            )).first();

            if (dbGenericDefaultInventory != null) {
                DefaultInventory dbDefaultInventory = dbGenericDefaultInventory.parseToDefaultInventory();

                Map<UUID, ItemStack> proved = new HashMap<>();
                for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> entry : dbDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                    if (localDefaultInventory.getDefaultItemsAsMap().containsKey(entry.getKey())) {
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> localEntry : localDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                            if (localEntry.getValue().getItemStack().equals(entry.getValue().getItemStack())) {
                                proved.put(localEntry.getValue().getUuid(), localEntry.getValue().getItemStack());
                            }
                        }
                    }
                }

                if (proved.size() != dbDefaultInventory.getDefaultItemsAsMap().size()) {
                    //Check if the data in the localDefaultInventory equals to there in the database
                    CoreSystem.getInstance().sendConsoleMessage("§cMerge all ModifiedInventories witch the DefaultInventory " + getInventoryKey(localDefaultInventory));
                    mergeModifiedInventories(localDefaultInventory, dbDefaultInventory);

                    defaultInventoriesCollection.replaceOne(combine(
                            eq("gamemode", modifyInventory.getGamemode().toString()),
                            eq("category", modifyInventory.getCategory()),
                            eq("name", modifyInventory.getName())
                    ), localDefaultInventory.parseToGenericInventory());
                }

                defaultInventories.put(modifyInventory.getClass().getName(), dbGenericDefaultInventory.parseToDefaultInventory());
            } else {
                //Insert new GenericInventory object in database!
                GenericDefaultInventory genericDefaultInventory = new GenericDefaultInventory(
                        System.currentTimeMillis() / 1000,
                        gamemode.toString(),
                        modifyInventory.getCategory(),
                        modifyInventory.getName(),
                        modifyInventory.getTitle(),
                        modifyInventory.getSize(),
                        eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager.toByteArray(modifyInventory.getUniqueItemStacks())
                );

                defaultInventoriesCollection.replaceOne(combine(
                        eq("gamemode", modifyInventory.getGamemode().toString()),
                        eq("category", modifyInventory.getCategory()),
                        eq("name", modifyInventory.getName())
                ), genericDefaultInventory, ReplaceOptions.createReplaceOptions(new UpdateOptions().upsert(true)));

                defaultInventories.put(modifyInventory.getClass().getName(), genericDefaultInventory.parseToDefaultInventory());
            }
        }
    }

    private Map<UUID, ModifiedInventory> mergeModifiedInventories(final DefaultInventory newDefaultInventory, final DefaultInventory oldDefaultInventory) {
        Map<UUID, ModifiedInventory> mergedInventories = new HashMap<>();
        Map<UUID, Map<String, ModifiedInventory>> modifiedInventories = new HashMap<>();

        //Loop all players in the collection
        for (Document userInfo : userInfoCollection.find()) {
            String playerUUID = userInfo.getString("uuid");
            String inventoryKey = getInventoryKey(newDefaultInventory);

            for (Map.Entry<String, Document> modifiedInventoryEntry : ((Map<String, Document>) userInfo.get("modifiedInventories")).entrySet()) {
                ModifiedInventory modifiedInventory = new ModifiedInventory(modifiedInventoryEntry.getValue());

                if (modifiedInventoryEntry.getKey().equalsIgnoreCase(inventoryKey)) {
                    HashMap<Integer, ItemStack> test = new HashMap<>();
                    HashMap<Integer, ModifyInventory.UniqueItemStack> addedItems = new HashMap<>();
                    HashMap<Integer, ModifyInventory.UniqueItemStack> removedItems = new HashMap<>();

                    //For added entries
                    for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> oldDefaultEntry : oldDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> newDefaultEntry : newDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                            if (oldDefaultEntry.getValue().getItemStack().equals(newDefaultEntry.getValue().getItemStack())) {
                                test.put(oldDefaultEntry.getKey(), oldDefaultEntry.getValue().getItemStack());
                            }
                        }
                    }

                    for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> newDefaultEntry : newDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                        if (!test.containsValue(newDefaultEntry.getValue().getItemStack())) {
                            addedItems.put(newDefaultEntry.getKey(), newDefaultEntry.getValue());
                        }
                    }

                    //For removed entries
                    test.clear();

                    for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> newDefaultEntry : newDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> oldDefaultEntry : oldDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                            if (newDefaultEntry.getValue().getItemStack().equals(oldDefaultEntry.getValue().getItemStack())) {
                                test.put(newDefaultEntry.getKey(), newDefaultEntry.getValue().getItemStack());
                            }
                        }
                    }

                    for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> oldDefaultEntry : oldDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                        if (!test.containsValue(oldDefaultEntry.getValue().getItemStack())) {
                            removedItems.put(oldDefaultEntry.getKey(), oldDefaultEntry.getValue());
                        }
                    }

                    Map<String, UUID> newUniqueItemStack = new HashMap<>(modifiedInventory.getUniqueItemStack());
                    //Loop all old entries in inventory
                    for (Map.Entry<String, UUID> modifiedUniqueItemsEntry : modifiedInventory.getUniqueItemStack().entrySet()) {

                        //Get the itemStack for the uuid
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> oldDefaultItemsEntry : oldDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                            //Check if the uuid is the current from the items entry
                            if (oldDefaultItemsEntry.getValue().getUuid().equals(modifiedUniqueItemsEntry.getValue())) {
                                ItemStack oldModifiedItemStack = oldDefaultItemsEntry.getValue().getItemStack();

                                //Get the new uuid for the ItemStack
                                for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> newDefaultItemsEntry : newDefaultInventory.getDefaultItemsAsMap().entrySet()) {
                                    if (newDefaultItemsEntry.getValue().getItemStack().equals(oldModifiedItemStack)) {
                                        UUID newUUID = newDefaultItemsEntry.getValue().getUuid();
                                        newUniqueItemStack.put(modifiedUniqueItemsEntry.getKey(), newUUID);
                                    }
                                }
                            }
                        }

                    }

                    //Remove Items
                    for (Map.Entry<String, UUID> modifiedEntry : modifiedInventory.getUniqueItemStack().entrySet()) {
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> removedEntry : removedItems.entrySet()) {
                            if (modifiedEntry.getValue().equals(removedEntry.getValue().getUuid())) {
                                newUniqueItemStack.remove(modifiedEntry.getKey());
                            }
                        }
                    }

                    //added Items
                    int added = 0;
                    for (Map.Entry<String, UUID> modifiedEntry : modifiedInventory.getUniqueItemStack().entrySet()) {
                        for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> addedEntry : addedItems.entrySet()) {
                            if (!modifiedEntry.getValue().equals(addedEntry.getValue().getUuid())) {
                                added++;
                                newUniqueItemStack.put(Integer.toString(addedEntry.getKey()), addedEntry.getValue().getUuid());
                            }

                            if (added >= addedItems.size()) {
                                break;
                            }
                        }
                    }

                    modifiedInventory.getUniqueItemStack().clear();
                    modifiedInventory.getUniqueItemStack().putAll(newUniqueItemStack);

                    //Add modified inventory to merged inventory map
                    mergedInventories.put(UUID.fromString(playerUUID), modifiedInventory);
                }

                if (modifiedInventories.containsKey(UUID.fromString(playerUUID))) {
                    modifiedInventories.get(UUID.fromString(playerUUID)).put(inventoryKey, modifiedInventory);
                } else {
                    modifiedInventories.put(UUID.fromString(playerUUID), new HashMap<String, ModifiedInventory>() {{
                        put(inventoryKey, modifiedInventory);
                    }});
                }
            }
        }

        for (Map.Entry<UUID, Map<String, ModifiedInventory>> modifiedInventoriesEntry : modifiedInventories.entrySet()) {
            userInfoCollection.updateOne(eq("uuid", modifiedInventoriesEntry.getKey().toString()), set("modifiedInventories", modifiedInventoriesEntry.getValue()));
            CoreSystem.getInstance().sendConsoleMessage("§aUpdate modifiedInventory for player §7" + modifiedInventoriesEntry.getKey());
        }

        return mergedInventories;
    }

    /**
     * Registers a List of ModifyInventory objects
     *
     * @param modifyInventory ModifyInventory
     */
    public void registerInventories(final ModifyInventory... modifyInventory) {
        for (ModifyInventory modifyInventories : modifyInventory) {
            registerInventory(modifyInventories);
        }
    }

    /**
     * Checks if the Inventory with the specified name belongs to an category with more inventories.
     *
     * @param clazz Inventory name
     * @return boolean
     */
    public boolean hasMultipleInventories(final Class<? extends ModifyInventory> clazz) {
        if (this.modifyInventories.containsKey(clazz)) {
            return this.modifyInventories.get(clazz).getCategory() != null;
        } else {
            return false;
        }
    }

    /**
     * Modifies a specified inventory for the specified uuid
     *
     * @param player          Bukkit Player
     * @param modifyInventory Bukkit Inventory
     */
    public boolean modifyInventory(final Player player, final ModifyInventory modifyInventory, final Inventory modifiedInventory) {
        try {
            Class<? extends ModifyInventory> modifiedInventoryClazz = modifyInventory.getClass();

            if (modifyInventories.containsKey(modifiedInventoryClazz)) {
                if (getInventory(modifiedInventoryClazz).getOptions().contains(CoreInventory.Option.CAN_MODIFY)) {
                    if (defaultInventories.containsKey(modifyInventory.getInventoryKey())) {
                        DefaultInventory defaultInventory = defaultInventories.get(modifyInventory.getInventoryKey());

                        ModifiedInventory modifiedInventoryObjc = new ModifiedInventory(
                                defaultInventory.getGamemode(),
                                defaultInventory.getCategory(),
                                defaultInventory.getName(),
                                defaultInventory.getTitle(),
                                defaultInventory.getSize(),
                                getUniqueItems(defaultInventory, modifiedInventory)
                        );

                        if (modifiedInventories.containsKey(player.getUniqueId())) {
                            if (modifiedInventories.get(player.getUniqueId()).containsKey(modifyInventory.getInventoryKey())) {
                                modifiedInventories.get(player.getUniqueId()).get(modifyInventory.getInventoryKey()).setUniqueItemStack(getUniqueItems(defaultInventory, modifiedInventory));
                            } else {
                                modifiedInventories.get(player.getUniqueId()).put(modifyInventory.getInventoryKey(), modifiedInventoryObjc);
                            }
                        } else {
                            modifiedInventories.put(player.getUniqueId(), new HashMap<String, ModifiedInventory>() {{
                                put(modifyInventory.getInventoryKey(), modifiedInventoryObjc);
                            }});
                        }

                        CoreSystem.getInstance().sendConsoleMessage("§aSave modified Inventory for Player " + player.getName());
                        return true;
                    } else {
                        throw new CoreException("No default inventory for inventory " + modifiedInventory.getName() + " registered!");
                    }
                }
            } else {
                throw new CoreException("No modifyInventory for inventory " + modifiedInventory.getName() + " registered!");
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Converts the items from default inventory to an Map
     *
     * @param defaultInventory target default inventory
     * @param inventory        inventory
     * @return Map
     */
    public Map<String, UUID> getUniqueItems(DefaultInventory defaultInventory, Inventory inventory) {
        Map<String, UUID> uniqueItems = new HashMap<>();
        int slot = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> entry : defaultInventory.getDefaultItemsAsMap().entrySet()) {
                if (entry.getValue().getItemStack().equals(itemStack)) {
                    uniqueItems.put(Integer.toString(slot), entry.getValue().getUuid());
                }
            }

            slot++;
        }

        return uniqueItems;
    }

    /**
     * Pushes the modifications for all local stored players
     */
    public void pushModifications() {
        if (!modifiedInventories.isEmpty()) {
            modifiedInventories.forEach((key, value) -> {
                if (userInfoCollection.updateOne(eq("uuid", key.toString()), set("modifiedInventories", modifiedInventories.get(key)), new UpdateOptions().upsert(true)).wasAcknowledged()) {
                    CoreSystem.getInstance().sendConsoleMessage("§aAll Inventory for §7" + key + " §apushed!");
                } else {
                    CoreSystem.getInstance().sendConsoleMessage("§cERROR: Error by replacing the ModifiedInventories row in the database, current UUID:" + key);
                }
            });
        }
    }

    /**
     * Pushes the modifications for the specified player
     *
     * @param player Bukkit Player
     */
    public void pushModifications(final Player player) {
        if (modifiedInventories.containsKey(player.getUniqueId())) {
            if (userInfoCollection.updateOne(eq("uuid", player.getUniqueId().toString()), set("modifiedInventories", modifiedInventories.get(player.getUniqueId())), new UpdateOptions().upsert(true)).wasAcknowledged()) {
                CoreSystem.getInstance().sendConsoleMessage("§aInventory for §7" + player.getUniqueId() + " §apushed!");
            } else {
                CoreSystem.getInstance().getMessager().send(player, "§cEs ist ein Fehler beim speicher des modifizierten Inventories aufgetreten!");
                CoreSystem.getInstance().sendConsoleMessage("§cERROR: Error by replacing the ModifiedInventories row in the database, current UUID:" + player.getUniqueId());
            }
        }
    }

    /**
     * Checks if the inventory for the Player uuid and name is modified
     *
     * @param uuid         Player uniqueID
     * @param inventoryKey The uniqueKey of the class {gamemode.category.name}
     * @return boolean
     */
    public boolean isInventoryModified(final UUID uuid, final String inventoryKey) {
        if (modifiedInventories.containsKey(uuid)) {
            return modifiedInventories.get(uuid).containsKey(inventoryKey);
        } else {
            return false;
        }
    }

    //Modify

    /**
     * Returns all registered ModifyInventories
     *
     * @return List of GameInventories
     */
    public List<ModifyInventory> getInventories() {
        return new ArrayList<>(modifyInventories.values());
    }

    /**
     * Returns a list of modifyInventories with out an category
     *
     * @return list of modify inventory
     */
    public List<ModifyInventory> getInventoriesWithOutCategory() {
        List<ModifyInventory> withOutCategory = new ArrayList<>();
        for (ModifyInventory modifyInventory : modifyInventories.values()) {
            if (modifyInventory.getCategory() == null) {
                withOutCategory.add(modifyInventory);
            }
        }

        return withOutCategory;
    }

    /**
     * Returns the GameInventory where the specified name
     *
     * @param clazz Inventory name
     * @return GameInventory object
     */
    public ModifyInventory getInventory(final Class<? extends ModifyInventory> clazz) {
        try {
            ModifyInventory modifyInventory = modifyInventories.getOrDefault(clazz, null);
            if (modifyInventory != null) {
                modifyInventory.getItems().clear();
                return modifyInventory;
            } else {
                throw new NullPointerException("Cannot get ModifyInventory for class, " + clazz.getName());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a list of ModifyInventory with the same Gamemode
     *
     * @param gamemode Gamemode Enum
     * @return List of ModifyInventory with the same gamemode
     */
    public List<ModifyInventory> getInventories(final Gamemode gamemode) {
        List<ModifyInventory> sameGamemode = new ArrayList<>();
        for (ModifyInventory modifyInventory : this.modifyInventories.values()) {
            if (modifyInventory.getGamemode() == gamemode) {
                sameGamemode.add(modifyInventory);
            }
        }

        return sameGamemode;
    }

    // Modified

    /**
     * Returns all modified inventories for the specified uuid
     *
     * @param uuid Player uniqueID
     * @return Map of Strings and ModifiedInventories
     */
    public Map<String, ModifiedInventory> getModifiedInventories(final UUID uuid) {
        if (modifiedInventories.containsKey(uuid)) {
            return modifiedInventories.get(uuid);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Returns the modified Inventory for the specified uuid and inventory name
     *
     * @param player          Bukkit Player
     * @param modifyInventory ModifyInventory instance
     * @return ModifiedInventory
     */
    public ModifiedInventory getModifiedInventory(final Player player, final ModifyInventory modifyInventory) {
        if (modifiedInventories.containsKey(player.getUniqueId())) {
            for (ModifiedInventory modifiedInventory : modifiedInventories.get(player.getUniqueId()).values()) {
                if (getInventoryKey(modifiedInventory).equalsIgnoreCase(modifyInventory.getInventoryKey())) {
                    return modifiedInventory;
                }
            }
        } else {
            return null;
        }

        return null;
    }

    /**
     * Returns a list of ModifiedInventories with the same category
     *
     * @param player   Bukkit Player
     * @param category Category of the inventory
     * @return List of ModifiedInventories with the same catgeory
     */
    public List<ModifiedInventory> getModifiedInventories(final Player player, final String category) {
        if (this.modifiedInventories.containsKey(player.getUniqueId())) {
            List<ModifiedInventory> sameCategory = new ArrayList<>();

            for (ModifiedInventory modifiedInventory : this.modifiedInventories.get(player.getUniqueId()).values()) {
                if (modifiedInventory.getCategory().equalsIgnoreCase(category)) {
                    sameCategory.add(modifiedInventory);
                }
            }

            return sameCategory;
        } else {
            return null;
        }
    }

    /**
     * Returns a list of modified inventories with the same gamemode
     *
     * @param player   Bukkit Player
     * @param gamemode Gamemode Enum
     * @return List of ModifiedInventories with the smae gamemode
     */
    public List<ModifiedInventory> getModifiedInventories(final Player player, final Gamemode gamemode) {
        if (this.modifiedInventories.containsKey(player.getUniqueId())) {
            List<ModifiedInventory> sameGamemode = new ArrayList<>();

            for (ModifiedInventory modifiedInventory : this.modifiedInventories.get(player.getUniqueId()).values()) {
                if (modifiedInventory.getGamemode().equalsIgnoreCase(gamemode.toString())) {
                    sameGamemode.add(modifiedInventory);
                }
            }

            return sameGamemode;
        } else {
            return new ArrayList<>();
        }
    }

    //Default

    /**
     * Returns a default inventory with the specified name
     *
     * @param inventoryKey UniqueKey of the inventory
     * @return DefaultInventory object
     */
    public DefaultInventory getDefaultInventory(final String inventoryKey) {
        try {
            DefaultInventory defaultInventory = defaultInventories.getOrDefault(inventoryKey, null);
            if (defaultInventory != null) {
                return defaultInventory;
            } else {
                throw new NullPointerException("Cannot found default inventory for key " + inventoryKey + "!");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns all list of all default inventories in the database
     *
     * @return List of DefaultInventories
     */
    public List<DefaultInventory> getDefaultInventories() {
        return new ArrayList<>(defaultInventories.values());
    }

    /**
     * Returns a list of DefaultInventories with the same category
     *
     * @param category Category of the Inventory
     * @return List of DefaultInventories with the same category
     */
    public List<DefaultInventory> getDefaultInventories(final String category) {
        List<DefaultInventory> defaultInventoriesWithCategory = new ArrayList<>();

        for (DefaultInventory defaultInventory : defaultInventories.values()) {
            if (defaultInventory.getCategory().equalsIgnoreCase(category)) {
                defaultInventoriesWithCategory.add(defaultInventory);
            }
        }

        return defaultInventoriesWithCategory;
    }

    /**
     * Returns a list of DefaultInventories with the same gamemode
     *
     * @param gamemode Gamemode Enum
     * @return List of DefaultInventories with the same gamemode
     */
    public List<DefaultInventory> getDefaultInventories(final Gamemode gamemode) {
        List<DefaultInventory> sameGamemode = new ArrayList<>();

        for (DefaultInventory defaultInventory : defaultInventories.values()) {
            if (defaultInventory.getGamemode().equalsIgnoreCase(gamemode.toString())) {
                sameGamemode.add(defaultInventory);
            }
        }

        return sameGamemode;
    }

    //Inventories with Categories

    /**
     * Returns a list of ModifyInventories were the specified category
     *
     * @param category Category
     * @return List of ModifyInventories with the same category
     */
    public List<ModifyInventory> getMultipleInventoriesWhereCategory(final String category) {
        return new ArrayList<>(multipleInventories.get(category).values());
    }

    /**
     * Returns modifyInventory inventories with the same gamemmode
     *
     * @param gamemode Bukkit Player
     * @return Map with ModifyInventory
     */
    public Map<String, Map<String, ModifyInventory>> getMultipleInventoriesWhereGamemode(final Gamemode gamemode) {
        Map<String, Map<String, ModifyInventory>> multipleInventories = new HashMap<>();
        for (Map.Entry<String, Map<String, ModifyInventory>> multipleEntry : this.multipleInventories.entrySet()) {
            for (Map.Entry<String, ModifyInventory> singleEntry : multipleEntry.getValue().entrySet()) {
                if (singleEntry.getValue().getGamemode().equals(gamemode)) {
                    if (multipleInventories.containsKey(multipleEntry.getKey())) {
                        multipleInventories.get(multipleEntry.getKey()).put(singleEntry.getKey(), singleEntry.getValue());
                    }
                    multipleInventories.put(multipleEntry.getKey(), new HashMap<String, ModifyInventory>() {{
                        put(singleEntry.getKey(), singleEntry.getValue());
                    }});
                }
            }
        }

        return multipleInventories;
    }

    /**
     * Returns a list of Game Inventories were the specified inventory name
     *
     * @param clazz Inventory name
     * @return List of ModifyInventories with the same category
     */
    public List<ModifyInventory> getInventoriesWithCategory(final Class<? extends ModifyInventory> clazz) {
        if (this.modifyInventories.containsKey(clazz)) {
            String category = this.modifyInventories.get(clazz).getCategory();
            if (category != null) {
                return new ArrayList<>(this.multipleInventories.get(category).values());
            }
        }

        return null;
    }

    /**
     * Returns a map of all modify inventories with an category
     *
     * @return Map<String, Map<String, ModifyInventory>>
     */
    public Map<String, Map<String, ModifyInventory>> getInventoriesWithCategories() {
        return multipleInventories;
    }

    /**
     * Sets for the specified player an modified inventory
     *
     * @param player          Bukkit Player
     * @param modifyInventory Currently Modifying
     * @return ModifyInventory
     */
    public ModifyInventory addCurrentlyModifying(Player player, ModifyInventory modifyInventory) {
        currentlyModifing.put(player, modifyInventory);
        return currentlyModifing.getOrDefault(player, null);
    }

    /**
     * Check if the player modified currently
     *
     * @param player Bukkit Player
     * @return boolean
     */
    public boolean isCurrentlyModifying(Player player) {
        return currentlyModifing.containsKey(player);
    }

    /**
     * Returns the currently modifying inventory for the player
     *
     * @param player Bukkit Player
     * @return Current ModifyInventory
     */
    public ModifyInventory getCurrentlyModifying(Player player) {
        return currentlyModifing.getOrDefault(player, null);
    }

    private String getInventoryKey(BasicInventory basicInventory) {
        return basicInventory.getGamemode() + "." + basicInventory.getCategory() + "." + basicInventory.getName();
    }

    /**
     * Returns the last modification date for the modified inventory
     *
     * @param player Bukkit Player
     * @param clazz  Inventory name
     * @return Date string
     */
    public String getLastUpdate(final Player player, final Class<? extends ModifyInventory> clazz) {
        if (modifiedInventories.containsKey(player.getUniqueId())) {
            for (ModifiedInventory modifiedInventory : modifiedInventories.get(player.getUniqueId()).values()) {
                if (modifiedInventory.getName().equalsIgnoreCase(clazz.getName())) {
                    return new SimpleDateFormat("dd.MM.yyy").format(new Date(modifiedInventory.getLastUpdate() * 1000));
                }
            }
        }

        return "§cNicht verfügbar!";
    }

    /**
     * Creates the ModificationInventory for the player
     *
     * @param player Bukkit Player
     */
    public void createModificationInventory(Player player) {
        new ModificationInventory().createInventory(player, gamemode);
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.modification;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemStack;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.ModificationCategoryInventory;
import eu.mcone.coresystem.bukkit.inventory.ModificationGamemodeInventory;
import eu.mcone.coresystem.bukkit.listener.InventoryModificationListener;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class CoreInventoryModificationManager implements InventoryModificationManager {

    @Getter
    private final Gamemode gamemode;

    private final Set<ModifyInventory> modifyInventories;
    private final Map<UUID, List<ModifiedInventory>> modifiedInventories;
    private final Map<Player, ModifyInventory> currentlyModifing;

    private final MongoCollection<Document> userInfoCollection;
    private final MongoCollection<DefaultInventory> defaultInventoriesCollection;

    public CoreInventoryModificationManager(final CorePlugin plugin) {
        this.gamemode = plugin.getGamemode();

        this.modifyInventories = new HashSet<>();
        this.modifiedInventories = new HashMap<>();
        this.currentlyModifing = new HashMap<>();

        //Collection in the data database for all modified inventories
        this.userInfoCollection = BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo");
        this.defaultInventoriesCollection = BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bukkitsystem_default_inventories", DefaultInventory.class);

        //Load all default Inventories where the specified gamemode
        plugin.registerEvents(new InventoryModificationListener(this));
        reload();
    }

    @Override
    public void reload() {
        modifyInventories.clear();
        modifiedInventories.clear();
        currentlyModifing.clear();

        if (gamemode.equals(Gamemode.UNDEFINED)) {
            for (DefaultInventory defaultInventory : defaultInventoriesCollection.find()) {
                CoreSystem.getInstance().sendConsoleMessage("§2Loading Default ModifiedInventory " + defaultInventory.getGamemode() + "." + defaultInventory.getCategory() + "." + defaultInventory.getName());
                modifyInventories.add(defaultInventory.toModifyInventory(this));
            }
        } else {
            for (DefaultInventory defaultInventory : defaultInventoriesCollection.find(eq("gamemode", gamemode.toString()))) {
                CoreSystem.getInstance().sendConsoleMessage("§2Loading Default ModifiedInventory " + defaultInventory.getGamemode() + "." + defaultInventory.getCategory() + "." + defaultInventory.getName());
                modifyInventories.add(defaultInventory.toModifyInventory(this));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            loadModifiedInventories(p.getUniqueId());
        }
    }

    @Override
    public void disable() {
        saveModifications();
    }

    /**
     * Loads all modified inventories for the specified uuid
     *
     * @param uuid Bukkit Player UUID
     */
    public void loadModifiedInventories(final UUID uuid) {
        Document inventories = userInfoCollection.find(eq("uuid", uuid.toString())).first();

        if (inventories != null) {
            modifiedInventories.put(uuid, new ArrayList<>());

            for (Document entry : inventories.getList("modifiedInventories", Document.class)) {
                modifiedInventories.get(uuid).add(new ModifiedInventory(entry));
            }
        } else {
            throw new RuntimeCoreException("Could not find Player with UUID " + uuid + " in userinfo database!");
        }
    }



    /*
     * Register Inventories
     */

    @Override
    public void registerInventories(final ModifyInventory... modifyInventories) {
        for (ModifyInventory modifyInventory : modifyInventories) {
            registerInventory(modifyInventory);
        }
    }

    @Override
    public void registerInventory(final ModifyInventory modifyInventory) {
        Map<Integer, UniqueItemStack> defaultItems = new HashMap<>();
        modifiedInvLoop:
        for (Map.Entry<Integer, CoreItemStack> modifiedInvEntry : modifyInventory.getItems().entrySet()) {
            for (Map.Entry<UUID, Integer> uuidIntegerEntry : modifyInventory.getUniqueItemStacks().entrySet()) {
                if (modifiedInvEntry.getKey().equals(uuidIntegerEntry.getValue())) {
                    defaultItems.put(modifiedInvEntry.getKey(), new UniqueItemStack(uuidIntegerEntry.getKey(), modifiedInvEntry.getValue().getItemStack()));
                    continue modifiedInvLoop;
                }
            }
        }

        DefaultInventory localDefaultInventory = new DefaultInventory(
                System.currentTimeMillis() / 1000,
                modifyInventory.getGamemode(),
                modifyInventory.getCategory(),
                modifyInventory.getName(),
                modifyInventory.getTitle(),
                modifyInventory.getSize(),
                defaultItems
        );
        ModifyInventory dbDefaultInventory = getModifyInventory(modifyInventory.getGamemode(), modifyInventory.getCategory(), modifyInventory.getName());

        if (dbDefaultInventory != null) {
            int proved = 0;

            dbLoop:
            for (CoreItemStack dbItem : dbDefaultInventory.getItems().values()) {
                for (CoreItemStack registeredItem : modifyInventory.getItems().values()) {
                    if (dbItem.getItemStack().equals(registeredItem.getItemStack())) {
                        proved++;
                        continue dbLoop;
                    }
                }
            }

            //Check if the data in the localDefaultInventory equals to there in the database
            if (dbDefaultInventory.getItems().size() != proved || modifyInventory.getItems().size() != proved) {
                CoreSystem.getInstance().sendConsoleMessage("§2Merge all ModifiedInventories with the DefaultInventory " + localDefaultInventory + "...");
                mergeModifiedInventories(localDefaultInventory, dbDefaultInventory);

                defaultInventoriesCollection.replaceOne(combine(
                        eq("gamemode", modifyInventory.getGamemode().toString()),
                        eq("category", modifyInventory.getCategory()),
                        eq("name", modifyInventory.getName())
                ), localDefaultInventory);

                modifyInventories.remove(dbDefaultInventory);
                modifyInventories.add(modifyInventory);
            } else {
                modifyInventories.remove(dbDefaultInventory);
                modifyInventories.add(new ModifyInventory(
                        this,
                        dbDefaultInventory.getGamemode(),
                        dbDefaultInventory.getUniqueItemStacks(),
                        modifyInventory.getItems(),
                        dbDefaultInventory.getName(),
                        dbDefaultInventory.getTitle(),
                        dbDefaultInventory.getCategory(),
                        dbDefaultInventory.getSize()
                ) {
                });
                CoreSystem.getInstance().sendConsoleMessage("§2Overwrite existing ModifyInventory " + localDefaultInventory + " with registered object...");
            }
        } else {
            modifyInventories.add(modifyInventory);
            defaultInventoriesCollection.replaceOne(combine(
                    eq("gamemode", modifyInventory.getGamemode().toString()),
                    eq("category", modifyInventory.getCategory()),
                    eq("name", modifyInventory.getName())
            ), localDefaultInventory, ReplaceOptions.createReplaceOptions(new UpdateOptions().upsert(true)));
        }
    }



    /*
     * Modify Inventories
     */

    @Override
    public List<ModifyInventory> getModifyInventories() {
        return new ArrayList<>(modifyInventories);
    }

    /**
     * Returns a default inventory with the specified name
     *
     * @param gamemode UniqueKey of the inventory
     * @return DefaultInventory object
     */
    public ModifyInventory getModifyInventory(Gamemode gamemode, String category, String name) {
        ModifyInventory result = null;

        for (ModifyInventory entry : modifyInventories) {
            if (entry.getGamemode().equals(gamemode)
                    && entry.getCategory().equals(category)
                    && entry.getName().equals(name)
            ) {
                result = entry;
            }
        }

        return result;
    }

    @Override
    public List<ModifyInventory> getModifyInventories(final Gamemode gamemode, final String category) {
        List<ModifyInventory> result = new ArrayList<>();

        for (ModifyInventory entry : modifyInventories) {
            if (entry.getGamemode().equals(gamemode) && entry.getCategory().equals(category)) {
                result.add(entry);
            }
        }

        return result;
    }


    /**
     * Returns a list of DefaultInventories with the same gamemode
     *
     * @param gamemode Gamemode Enum
     * @return List of DefaultInventories with the same gamemode
     */
    @Override
    public List<ModifyInventory> getModifyInventories(final Gamemode gamemode) {
        List<ModifyInventory> result = new ArrayList<>();

        for (ModifyInventory inv : modifyInventories) {
            if (inv.getGamemode().equals(gamemode)) {
                result.add(inv);
            }
        }

        return result;
    }

    @Override
    public Set<String> getModifyInventoryCategories(Gamemode gamemode) {
        Set<String> categories = new HashSet<>();

        for (ModifyInventory inv : getModifyInventories(gamemode)) {
            categories.add(inv.getCategory());
        }

        return categories;
    }

    @Override
    public void modifyInventory(final Player player, final ModifyInventory modifyInventory, final Inventory modifiedInventory) {
        if (getModifyInventory(modifyInventory.getGamemode(), modifyInventory.getCategory(), modifyInventory.getName()) != null) {

            ModifiedInventory modifiedInventoryObj = new ModifiedInventory(
                    System.currentTimeMillis() / 1000,
                    modifyInventory.getGamemode(),
                    modifyInventory.getCategory(),
                    modifyInventory.getName(),
                    modifyInventory.getTitle(),
                    modifyInventory.getSize(),
                    getUniqueItems(modifyInventory, modifiedInventory)
            );

            if (modifiedInventories.containsKey(player.getUniqueId())) {
                ModifiedInventory duplicate = getModifiedInventory(player.getUniqueId(), modifyInventory.getGamemode(), modifyInventory.getCategory(), modifyInventory.getName());
                if (duplicate != null) {
                    modifiedInventories.get(player.getUniqueId()).remove(duplicate);
                }

                modifiedInventories.get(player.getUniqueId()).add(modifiedInventoryObj);
            } else {
                modifiedInventories.put(player.getUniqueId(), new ArrayList<>(Collections.singletonList(modifiedInventoryObj)));
            }

            saveModifications(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            CoreSystem.getInstance().getMessager().send(player, "§2Du hast das Inventar " + modifyInventory.getName() + " §2erfolgreich modifiziert!");
        } else {
            throw new RuntimeCoreException("No modifyInventory for inventory " + modifiedInventory.getName() + " registered!");
        }
    }

    @Override
    public boolean isCurrentlyModifying(final Player player) {
        return currentlyModifing.containsKey(player);
    }

    @Override
    public ModifyInventory getCurrentlyModifyingInventory(final Player player) {
        return currentlyModifing.getOrDefault(player, null);
    }

    @Override
    public ModifyInventory setCurrentlyModifying(final Player player, final ModifyInventory inventory) {
        currentlyModifing.put(player, inventory);
        return inventory;
    }

    @Override
    public void removeCurrentlyModifying(final Player player) {
        currentlyModifing.remove(player);
    }

    @Override
    public void openGamemodeModificationInventory(final Player player) {
        new ModificationGamemodeInventory(this, player).openInventory();
    }

    @Override
    public void openCategoryModificationInventory(final Player player, final Gamemode gamemode) {
        new ModificationCategoryInventory(this, player, gamemode).openInventory();
    }

    @Override
    public void openCategoryModificationInventory(final Player player, final Gamemode gamemode, String category) {
        new ModificationCategoryInventory(this, player, gamemode, category).openInventory();
    }

    @Override
    public void openModifyInventory(final Player player, final String category, final String name) throws RuntimeCoreException {
        ModifyInventory inventory = getModifyInventory(gamemode, category, name);

        if (inventory != null) {
            inventory.openInventory(player);
        } else {
            throw new RuntimeCoreException("No ModifyInventory with category " + category + " and name " + name + " existing!");
        }
    }

    @Override
    public boolean hasInventoryModified(final UUID uuid, final ModifyInventory inv) {
        return getModifiedInventory(uuid, inv.getGamemode(), inv.getCategory(), inv.getName()) != null;
    }

    /*
     * Modified Inventories
     */

    public ModifiedInventory getModifiedInventory(final UUID uuid, final Gamemode gamemode, final String category, final String name) {
        ModifiedInventory result = null;

        for (ModifiedInventory entry : modifiedInventories.get(uuid)) {
            if (entry.getGamemode().equals(gamemode)
                    && entry.getCategory().equals(category)
                    && entry.getName().equals(name)
            ) {
                result = entry;
            }
        }

        return result;
    }

    /**
     * Returns the modified Inventory for the specified uuid and ModifyInventory object
     *
     * @param uuid            Bukkit Player UUID
     * @param modifyInventory ModifyInventory instance
     * @return ModifiedInventory
     */
    public ModifiedInventory getModifiedInventory(final UUID uuid, final ModifyInventory modifyInventory) {
        return getModifiedInventory(uuid, modifyInventory.getGamemode(), modifyInventory.getCategory(), modifyInventory.getName());
    }

    /**
     * Returns the players modified items of an inventory
     * returns null if the player ha not modified this inventory
     *
     * @param uuid              Bukkit Player UUID
     * @param modifiedInventory ModifyInventory which the target player should have modified
     * @return the players modified items of an inventory
     */
    @Override
    public Map<String, UUID> getModifiedInventoryItems(final UUID uuid, final ModifyInventory modifiedInventory) {
        ModifiedInventory inv = getModifiedInventory(uuid, modifiedInventory);
        return inv != null ? inv.getUniqueItemStacks() : null;
    }

    /**
     * Returns all modified inventories for the specified uuid
     *
     * @param uuid Bukkit Player UUID
     * @return Map of Strings and ModifiedInventories
     */
    public List<ModifiedInventory> getModifiedInventories(final UUID uuid) {
        return modifiedInventories.getOrDefault(uuid, Collections.emptyList());
    }

    /**
     * Returns a list of ModifiedInventories with the same category
     *
     * @param uuid     Bukkit Player UUID
     * @param category Category of the inventory
     * @return List of ModifiedInventories with the same catgeory
     */
    public List<ModifiedInventory> getModifiedInventories(final UUID uuid, final Gamemode gamemode, final String category) {
        List<ModifiedInventory> result = new ArrayList<>();

        for (ModifiedInventory inv : modifiedInventories.getOrDefault(uuid, Collections.emptyList())) {
            if (inv.getGamemode().equals(gamemode) && inv.getCategory().equals(category)) {
                result.add(inv);
            }
        }

        return result;
    }

    /**
     * Returns a list of modified inventories with the same gamemode
     *
     * @param uuid     Bukkit Player UUID
     * @param gamemode Gamemode Enum
     * @return List of ModifiedInventories with the smae gamemode
     */
    public List<ModifiedInventory> getModifiedInventories(final UUID uuid, final Gamemode gamemode) {
        List<ModifiedInventory> result = new ArrayList<>();

        for (ModifiedInventory inv : modifiedInventories.getOrDefault(uuid, Collections.emptyList())) {
            if (inv.getGamemode().equals(gamemode)) {
                result.add(inv);
            }
        }

        return result;
    }

    /**
     * Pushes the modifications for all local stored players
     */
    private void saveModifications() {
        for (UUID uuid : modifiedInventories.keySet()) {
            saveModifications(uuid);
        }
    }

    @Override
    public void saveModifications(UUID uuid) {
        if (modifiedInventories.containsKey(uuid)) {
            userInfoCollection.updateOne(
                    eq("uuid", uuid.toString()),
                    set("modifiedInventories", modifiedInventories.get(uuid)),
                    new UpdateOptions().upsert(true)
            );
        }
    }

    /*
     * Util
     */

    public void unloadPlayer(Player player) {
        modifiedInventories.remove(player.getUniqueId());
        currentlyModifing.remove(player);
    }

    /**
     * Parse the Map to an byte array
     *
     * @param defaultItems HashMap
     * @return Byte array
     */
    static byte[] toByteArray(Map<Integer, UniqueItemStack> defaultItems) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            //Convert ItemStack
            dataOutput.writeInt(defaultItems.size());

            for (Map.Entry<Integer, UniqueItemStack> entry : defaultItems.entrySet()) {
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
    static Map<Integer, UniqueItemStack> fromByteArray(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Map<Integer, UniqueItemStack> defaultItems = new HashMap<>();

            int size = dataInput.readInt();
            for (int i = 0; i < size; i++) {
                defaultItems.put(dataInput.readInt(), (UniqueItemStack) dataInput.readObject());
            }

            return defaultItems;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void mergeModifiedInventories(final DefaultInventory newDefaultInventory, final ModifyInventory oldDefaultInventory) {
        //Loop all players in the collection
        playerLoop:
        for (Document userInfo : userInfoCollection.find()) {
            UUID playerUUID = UUID.fromString(userInfo.getString("uuid"));
            ModifiedInventory modifiedInventory = null;

            List<Document> modifiedInventories = userInfo.getList("modifiedInventories", Document.class);
            for (Document entry : modifiedInventories) {
                if (newDefaultInventory.getGamemode().equals(Gamemode.valueOf(entry.getString("gamemode")))
                        && newDefaultInventory.getCategory().equals(entry.getString("category"))
                        && newDefaultInventory.getName().equals(entry.getString("name"))
                ) {
                    modifiedInventory = new ModifiedInventory(entry);
                    break;
                }
            }

            if (modifiedInventory != null) {
                userInfoCollection.updateOne(
                        eq("uuid", playerUUID.toString()),
                        pull("modifiedInventories", combine(
                                eq("gamemode", newDefaultInventory.getGamemode().toString()),
                                eq("category", newDefaultInventory.getCategory()),
                                eq("name", newDefaultInventory.getName())
                        ))
                );

                HashMap<Integer, ItemStack> equalItems = new HashMap<>();
                HashMap<Integer, UniqueItemStack> addedItems = new HashMap<>();
                HashMap<Integer, UniqueItemStack> removedItems = new HashMap<>();

                //For equal entries
                equalLoop:
                for (Map.Entry<Integer, CoreItemStack> oldDefaultEntry : oldDefaultInventory.getItems().entrySet()) {
                    for (Map.Entry<Integer, UniqueItemStack> newDefaultEntry : newDefaultInventory.calculateDefaultItems().entrySet()) {
                        if (oldDefaultEntry.getValue().getItemStack().equals(newDefaultEntry.getValue().getItemStack())) {
                            equalItems.put(oldDefaultEntry.getKey(), oldDefaultEntry.getValue().getItemStack());
                            continue equalLoop;
                        }
                    }
                }

                //calculate added entries
                for (Map.Entry<Integer, UniqueItemStack> newDefaultEntry : newDefaultInventory.calculateDefaultItems().entrySet()) {
                    if (!equalItems.containsValue(newDefaultEntry.getValue().getItemStack())) {
                        addedItems.put(
                                newDefaultEntry.getKey(),
                                newDefaultEntry.getValue()
                        );
                    }
                }

                //calculate removed entries
                for (Map.Entry<Integer, CoreItemStack> oldDefaultEntry : oldDefaultInventory.getItems().entrySet()) {
                    if (!equalItems.containsValue(oldDefaultEntry.getValue().getItemStack())) {
                        removedItems.put(
                                oldDefaultEntry.getKey(),
                                new UniqueItemStack(oldDefaultInventory.getUuidForSlot(oldDefaultEntry.getKey()), oldDefaultEntry.getValue().getItemStack())
                        );
                    }
                }


                //Calculate new UUIDs for current item slots
                Map<String, UUID> newUniqueItemStacks = new HashMap<>(modifiedInventory.getUniqueItemStacks());

                newUuidLoop:
                //Loop all old entries from database inventory
                for (Map.Entry<String, UUID> databaseUniqueItemStacks : modifiedInventory.getUniqueItemStacks().entrySet()) {
                    //For old UniqueItemStacks -> Get ItemStack for old UUID
                    for (Map.Entry<Integer, CoreItemStack> oldDefaultItemsEntry : oldDefaultInventory.getItems().entrySet()) {
                        //Check if the uuid is the current from the items entry
                        if (oldDefaultInventory.getUuidForSlot(oldDefaultItemsEntry.getKey()).equals(databaseUniqueItemStacks.getValue())) {
                            ItemStack oldModifiedItemStack = oldDefaultItemsEntry.getValue().getItemStack();

                            //Get the new uuid for the ItemStack
                            for (Map.Entry<Integer, UniqueItemStack> newDefaultItemsEntry : newDefaultInventory.calculateDefaultItems().entrySet()) {
                                if (newDefaultItemsEntry.getValue().getItemStack().equals(oldModifiedItemStack)) {
                                    UUID newUUID = newDefaultItemsEntry.getValue().getUuid();
                                    newUniqueItemStacks.put(databaseUniqueItemStacks.getKey(), newUUID);
                                    continue newUuidLoop;
                                }
                            }
                        }
                    }
                }

                //Remove Items
                for (Map.Entry<Integer, UniqueItemStack> removeEntry : removedItems.entrySet()) {
                    newUniqueItemStacks.remove(Integer.toString(removeEntry.getKey()));
                }

                //Add Items
                for (Map.Entry<Integer, UniqueItemStack> addEntry : addedItems.entrySet()) {
                    if (newUniqueItemStacks.containsKey(Integer.toString(addEntry.getKey()))) {
                        continue playerLoop;
                    } else {
                        newUniqueItemStacks.put(Integer.toString(addEntry.getKey()), addEntry.getValue().getUuid());
                    }
                }

                modifiedInventory.setUniqueItemStacks(newUniqueItemStacks);
                userInfoCollection.updateOne(eq("uuid", playerUUID), push("modifiedInventories", modifiedInventory));
            }
        }
    }

    private Map<String, UUID> getUniqueItems(ModifyInventory modifyInventory, Inventory inventory) {
        Map<String, UUID> uniqueItems = new HashMap<>();
        int slot = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            for (Map.Entry<UUID, Integer> entry : modifyInventory.getUniqueItemStacks().entrySet()) {
                if (modifyInventory.getItems().get(entry.getValue()).getItemStack().equals(itemStack)) {
                    uniqueItems.put(Integer.toString(slot), entry.getKey());
                }
            }

            slot++;
        }

        return uniqueItems;
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import eu.mcone.coresystem.api.bukkit.config.CoreJsonConfig;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CorePlayerDataStorage extends CoreJsonConfig {

    public CorePlayerDataStorage(BukkitCoreSystem instance) {
        super(instance, "playerdata.json");
    }

    public void updateEnderchestItems(UUID uuid, Inventory enderchest) {
        Map<String, ItemStack> items = new HashMap<>();

        for (int i = 0; i < enderchest.getSize(); i++) {
            if (enderchest.getItem(i) != null) {
                items.put(String.valueOf(i), enderchest.getItem(i));
            }
        }

        insertPlayerIfNotExists(uuid);
        getJson().getAsJsonObject().getAsJsonObject(uuid.toString()).add(
                "enderchest",
                PRETTY_GSON.toJsonTree(
                        items,
                        new TypeToken<HashMap<String, ItemStack>>(){}.getType()
                )
        );

        save();
    }

    @SuppressWarnings("unchecked")
    public Inventory getEnderchestItems(Player p) {
        JsonObject o = getJson().getAsJsonObject();
        Inventory enderchest = Bukkit.createInventory(null, InventorySlot.ROW_6, "§7Deine Enderkiste");

        if (o.has(p.getUniqueId().toString()) && o.getAsJsonObject(p.getUniqueId().toString()).has("enderchest")) {
            for (Map.Entry<String, ItemStack> e : ((HashMap<String, ItemStack>) PRETTY_GSON.fromJson(
                    o.getAsJsonObject(p.getUniqueId().toString()).getAsJsonObject("enderchest"),
                    new TypeToken<HashMap<String, ItemStack>>(){}.getType())
            ).entrySet()) {
                enderchest.setItem(Integer.parseInt(e.getKey()), e.getValue());
            }
        }

        return enderchest;
    }

    public void setHome(UUID uuid, String name, Location location) {
        insertPlayerIfNotExists(uuid);
        getJson().getAsJsonObject().getAsJsonObject(uuid.toString()).getAsJsonObject("homes").add(
                name,
                PRETTY_GSON.toJsonTree(location, Location.class)
        );

        save();
    }

    public void removeHome(UUID uuid, String name) {
        getJson().getAsJsonObject().getAsJsonObject(uuid.toString()).getAsJsonObject("homes").remove(name);
        save();
    }

    public Map<String, Location> getHomes(UUID uuid) {
        JsonObject o = getJson().getAsJsonObject();

        if (o.has(uuid.toString()) && o.getAsJsonObject(uuid.toString()).has("homes")) {
            return PRETTY_GSON.fromJson(
                    o.getAsJsonObject(uuid.toString()).getAsJsonObject("homes"),
                    new TypeToken<HashMap<String, Location>>(){}.getType()
            );
        } else {
            return Collections.emptyMap();
        }
    }

    private void insertPlayerIfNotExists(UUID uuid) {
        if (!getJson().getAsJsonObject().has(uuid.toString())) {
            JsonObject o = new JsonObject();
            o.add("homes", new JsonObject());
            o.add("enderchest", new JsonObject());

            getJson().getAsJsonObject().add(uuid.toString(), o);
        }
    }

}

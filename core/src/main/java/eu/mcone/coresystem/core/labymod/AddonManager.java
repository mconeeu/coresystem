/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.labymod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.core.labymod.LabyModAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddonManager {

    /**
     * Parses the addons from the INFO plugin message
     *
     * @param jsonObject the json object of the message
     * @return a list containing the message's addons
     */
    public static List<LabyModAddon> getAddons(JsonObject jsonObject) {
        if (!jsonObject.has("addons") || !jsonObject.get("addons").isJsonArray())
            return new ArrayList<>();

        List<LabyModAddon> addons = new ArrayList<>();

        for (JsonElement arrayElement : jsonObject.get("addons").getAsJsonArray()) {
            if (!arrayElement.isJsonObject())
                continue;

            JsonObject arrayObject = arrayElement.getAsJsonObject();

            if (!arrayObject.has("uuid") || !arrayObject.get("uuid").isJsonPrimitive() || !arrayObject.get("uuid").getAsJsonPrimitive().isString()
                    || !arrayObject.has("name") || !arrayObject.get("name").isJsonPrimitive() || !arrayObject.get("name").getAsJsonPrimitive().isString())
                continue;

            UUID uuid;

            try {
                uuid = UUID.fromString(arrayObject.get("uuid").getAsString());
            } catch (IllegalArgumentException ex) {
                continue;
            }

            addons.add(new LabyModAddon(uuid, arrayObject.get("name").getAsString()));
        }

        return addons;
    }

}

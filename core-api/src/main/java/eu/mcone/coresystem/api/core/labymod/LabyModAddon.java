/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * An Addon represents a player's addon
 * The addons are being sent when a user joins the server
 * You can retrieve them by using LabyModPlayerJoinEvent#getAddons()
 *
 * @author Jan
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public final class LabyModAddon {

    private final UUID uuid;
    private final String name;
    @Setter
    private boolean required;

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("uuid", uuid.toString());
        obj.addProperty("required", required);

        return obj;
    }

}

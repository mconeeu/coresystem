/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class PermissionChangeEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private Type type;
    private final CorePlayer player;
    private Set<Group> groups;

    public enum Type {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(CorePlayer player, String[] data) {
        this.player = player;

        if (data.length >= 1) {
            type = Type.valueOf(data[0]);

            if (!type.equals(Type.USER_PERMISSION)) {
                groups = new HashSet<>();
                JsonArray array = CoreSystem.getInstance().getJsonParser().parse(data[1]).getAsJsonArray();

                for (JsonElement e : array) {
                    groups.add(Group.getGroupById(e.getAsInt()));
                }
            }
        }
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}

/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

public final class PermissionChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private Kind kind;
    @Getter
    private BukkitCorePlayer player;
    @Getter
    private Set<Group> groups;

    public enum Kind {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(BukkitCorePlayer player, String[] data) {
        this.player = player;

        if (data.length >= 1) {
            kind = Kind.valueOf(data[0]);

            if (!kind.equals(Kind.USER_PERMISSION)) {
                groups = new HashSet<>();
                JsonArray array = new JsonParser().parse(data[1]).getAsJsonArray();

                for (JsonElement e : array) {
                    groups.add(Group.getGroupById(e.getAsInt()));
                }
            }
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

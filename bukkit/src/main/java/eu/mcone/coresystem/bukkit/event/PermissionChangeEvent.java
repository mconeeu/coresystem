/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.event;

import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PermissionChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Kind kind;
    private CorePlayer player;
    private Group group;

    public enum Kind {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(CorePlayer player, String[] data) {
        this.player = player;

        if (data.length >= 1) {
            kind = Kind.valueOf(data[0]);
            if (!kind.equals(Kind.USER_PERMISSION)) group = Group.getGroupbyName(data[1]);
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Kind getKind() {
        return kind;
    }

    public CorePlayer getPlayer() {
        return player;
    }

    public Group getGroup() {
        return group;
    }

}

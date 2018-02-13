/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.event;

import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import net.md_5.bungee.api.plugin.Event;

public class PermissionChangeEvent extends Event {

    private Kind kind;
    private CorePlayer player;
    private Group group;

    public enum Kind {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(Kind k, CorePlayer p) {
        this.kind = k;
        this.player = p;
    }

    public PermissionChangeEvent(Kind k, CorePlayer p, Group group) {
        this.kind = k;
        this.player = p;
        this.group = group;
    }

    public Kind getKind() {
        return kind;
    }

    public Group getGroup() {
        return group;
    }

    public CorePlayer getPlayer() {
        return player;
    }
}

/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.event;

import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PermissionChangeEvent extends Event {

    @Getter
    private Kind kind;
    @Getter
    private CorePlayer player;
    @Getter
    private Set<Group> groups;

    public enum Kind {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(Kind k, CorePlayer p) {
        this.kind = k;
        this.player = p;
    }

    public PermissionChangeEvent(Kind k, CorePlayer p, Set<Group> groups) {
        this.kind = k;
        this.player = p;
        this.groups = groups;
    }

    public PermissionChangeEvent(Kind k, Group group) {
        this.kind = k;
        this.groups = new HashSet<>(Collections.singletonList(group));
    }

}

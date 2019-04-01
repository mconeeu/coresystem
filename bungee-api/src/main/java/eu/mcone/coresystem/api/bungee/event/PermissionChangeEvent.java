/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public final class PermissionChangeEvent extends Event {

    private final Kind kind;
    private final CorePlayer player;
    private final Set<Group> groups;

    public enum Kind {
        USER_PERMISSION,
        GROUP_PERMISSION,
        GROUP_CHANGE
    }

    public PermissionChangeEvent(Kind k, CorePlayer p) {
        this.kind = k;
        this.player = p;
        this.groups = null;
    }

    public PermissionChangeEvent(Kind k, Group group) {
        this.kind = k;
        this.player = null;
        this.groups = new HashSet<>(Collections.singletonList(group));
    }

}

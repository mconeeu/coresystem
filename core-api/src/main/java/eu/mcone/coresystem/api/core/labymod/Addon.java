/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * An Addon represents a player's addon
 * The addons are being sent when a user joins the server
 * You can retrieve them by using LabyModPlayerJoinEvent#getAddons()
 *
 * @author Jan
 */
@AllArgsConstructor
@Getter
public final class Addon {

    private final UUID uuid;
    private final String name;

}

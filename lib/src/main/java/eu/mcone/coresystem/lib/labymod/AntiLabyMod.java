/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.labymod;

import java.util.HashMap;
import java.util.Map;

public class AntiLabyMod {

    private Map<LabyPermission, Boolean> permissions;

    public AntiLabyMod() {
        permissions = new HashMap<>();

        for (LabyPermission permission : LabyPermission.values()) {
            permissions.put(permission, permission.isDefaultEnabled());
        }
    }

    public AntiLabyMod set(LabyPermission permission, boolean allow) {
        permissions.put(permission, allow);
        return this;
    }

    public void send(PacketSender sender) {
        sender.sendPacket("LMC", LabyModAPI.getBytesToSend(permissions));
    }

}

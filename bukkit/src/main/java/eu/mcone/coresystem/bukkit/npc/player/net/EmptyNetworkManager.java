/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.player.net;

import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketAddress;

public class EmptyNetworkManager extends NetworkManager {

    private static final Field NETWORK_ADDRESS = ReflectionManager.getField(NetworkManager.class, "l");

    public EmptyNetworkManager(EnumProtocolDirection flag) throws IOException {
        super(flag);

        if (NETWORK_ADDRESS == null)
            return;
        try {
            channel = new EmptyChannel(null);
            NETWORK_ADDRESS.set(this, new SocketAddress() {
                private static final long serialVersionUID = 8207338859896320185L;
            });
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean g() {
        return true;
    }

}

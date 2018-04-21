/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.labymod;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import io.netty.buffer.ByteBuf;

import java.util.Map;

public interface LabyModAPI {

    void sendMessage(GlobalCorePlayer player, String messageKey, String messageContents);

    void setLabyModPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions);

    int readVarIntFromBuffer(ByteBuf buf);

    String readString(ByteBuf buf, int maxLenght);

}

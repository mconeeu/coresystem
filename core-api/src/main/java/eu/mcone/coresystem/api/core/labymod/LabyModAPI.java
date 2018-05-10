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

    /**
     * sends message to LabyMod
     * @param player target player
     * @param messageKey key
     * @param messageContents content
     */
    void sendMessage(GlobalCorePlayer player, String messageKey, String messageContents);

    /**
     * sends LabyMod permissions
     * @param player target player
     * @param permissions permission map
     */
    void setLabyModPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions);

    /**
     * read int from LabyMod channel ByteBuf
     * @param buf byte buffer
     * @return Integer
     */
    int readVarIntFromBuffer(ByteBuf buf);

    /**
     * read String from LabyMod channel ByteBuf
     * @param buf byte buffer
     * @param maxLenght max length of String
     * @return String
     */
    String readString(ByteBuf buf, int maxLenght);

}

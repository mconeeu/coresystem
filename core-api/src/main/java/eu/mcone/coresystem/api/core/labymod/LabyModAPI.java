/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.UUID;

public interface LabyModAPI<P> {

    /**
     * sends a list of permissions to the LabyMod client to forbid specific functions
     *
     * @param player      target player
     * @param permissions permission map
     */
    void sendPermissions(P player, Map<LabyModPermission, Boolean> permissions);

    void unsetCurrentServer(P player);

    void setCurrentServer(P receiver, String gamemodeName);

    void unsetCurrentGameInfo(P player);

    void setCurrentGameInfo(P player, String gamemode, long startTime, long endTime);

    void unsetPartyInfo(P player);

    void setPartyInfo(P player, UUID partyLeaderUUID, int partySize, int maxPartyMembers);

    void setSubtitle(P receiver, UUID subtitlePlayer, String value);

    void recommendAddons(P player, LabyModAddon... addons);

    void setMiddleClickActions(P player, LabyModMiddleClickAction... actionList);

    void setBalanceDisplay(P player, LabyModBalanceType balanceType, int balance);

    void unsetBalanceDisplay(P player);

    void setCineScopes(P player, int coveragePercent, long duration);

    void unsetCineScopes(P player);

    void setWatermark(P player, boolean visible);

    void setVoiceChatAllowed(P player, boolean allowed);

    void setVoiceChatSettings(P player, boolean required, boolean enabled, int microphoneVolume, int surroundRange, int surroundVolume, boolean continuousTransmission);

    void setPlayerMutedFor(P player, UUID mutedPlayer, boolean muted);

    /**
     * sends a specific message to the LabyMod client that can be read i.e. with a LabyMod AddOn
     *
     * @param player     target player
     * @param messageKey message key
     * @param json       message packets in JSON format
     */
    void sendServerMessage(P player, String messageKey, JsonElement json);

    void sendClientToServer(P player, String title, String address, boolean preview);
}

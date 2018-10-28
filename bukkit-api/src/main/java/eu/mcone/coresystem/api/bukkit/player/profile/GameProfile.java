/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

public interface GameProfile {

    /**
     * returns the profile Name of the GameProfile
     * @return ProfileName
     */
    String getProfileName();

    /**
     * returns the game section name of the CorePlugin
     * @return GameSectionName
     */
    String getGameSectionName();

    /**
     * returns the game profile type of the GameProfile
     * @return GameProfileType
     */
    GameProfileType getGameProfileType();

}

/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.UUID;

public interface CoinsAPI {

    /**
     * check if a player is registered in the mc one database
     * @param name name
     * @return boolean registered
     */
    boolean isRegistered(String name);


    int getCoins(UUID uuid);

    int getCoins(String name);


    void setCoins(UUID uuid, int coins);

    void setCoins(String name, int coins);


    void addCoins(UUID uuid, int coins);

    void addCoins(String name, int coins);


    void removeCoins(UUID uuid, int coins);

    void removeCoins(String name, int coins);

}

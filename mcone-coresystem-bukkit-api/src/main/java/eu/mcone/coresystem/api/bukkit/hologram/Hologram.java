/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import org.bukkit.entity.Player;

public interface Hologram {

    void showPlayerTemp(Player player, int time);

    void showAllTemp(int time);

    void showPlayer(Player p);

    void hidePlayer(Player p);

    void showAll();

}

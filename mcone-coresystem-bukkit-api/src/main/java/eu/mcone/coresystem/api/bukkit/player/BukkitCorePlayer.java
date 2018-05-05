/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import org.bukkit.entity.Player;

public interface BukkitCorePlayer extends GlobalCorePlayer {

    String getStatus();

    String getNickname();

    void setNickname(String nickname);

    CoreScoreboard getScoreboard();

    Player bukkit();

    void setScoreboard(CoreScoreboard scoreboard);

    void setStatus(String status);

}

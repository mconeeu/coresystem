/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch.replay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.Server;

@Getter
@Setter
@AllArgsConstructor
public class ReplayServerSession {

    private long registered;
    private String replayID;
    private Server server;
    private String gamemode;

}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch.replay;

import net.md_5.bungee.api.connection.Server;

import java.util.HashMap;

public class ReplayServerSessionHandler {

    private final HashMap<String, ReplayServerSession> replayServerSessions;

    public ReplayServerSessionHandler() {
        replayServerSessions = new HashMap<>();
    }

    public void addReplayServer(final ReplayServerSession session) {
        replayServerSessions.put(session.getServer().getInfo().getName(), session);
    }

    public void registerReplayServer(final String replayID, final Server server, final String gamemode) {
        replayServerSessions.put(server.getInfo().getName(), new ReplayServerSession(System.currentTimeMillis() / 1000, replayID, server, gamemode));
    }

    public void unRegisterServer(final Server server) {
        replayServerSessions.remove(server.getInfo().getName());
    }

    public String getReplayID(final Server server) {
        return replayServerSessions.get(server.getInfo().getName()).getReplayID();
    }

    public boolean hasReplayID(final Server server) {
        return replayServerSessions.containsKey(server.getInfo().getName());
    }
}

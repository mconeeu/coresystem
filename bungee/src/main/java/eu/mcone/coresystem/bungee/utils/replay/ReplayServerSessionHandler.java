package eu.mcone.coresystem.bungee.utils.replay;

import net.md_5.bungee.api.connection.Server;

import java.util.HashMap;

public class ReplayServerSessionHandler {

    private HashMap<String, ServerSession> replayServerSessions;

    public ReplayServerSessionHandler() {
        replayServerSessions = new HashMap<>();
    }

    public void registerReplayServer(final Server server, final String gamemode) {
        replayServerSessions.put(server.getInfo().getName(), new ServerSession(System.currentTimeMillis() / 1000, server, gamemode));
    }

    public void unRegisterServer(final Server server) {
        replayServerSessions.remove(server.getInfo().getName());
    }

    public boolean useReplaySystem(final Server server) {
        return replayServerSessions.containsKey(server.getInfo().getName());
    }
}

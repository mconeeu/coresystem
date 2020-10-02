package eu.mcone.coresystem.bungee.utils;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.util.CoreDebugger;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeDebugger extends CoreDebugger<ProxiedPlayer> implements eu.mcone.coresystem.api.bungee.util.BungeeDebugger  {

    public BungeeDebugger(BungeeCoreSystem bungeeCoreSystem) {
        super(bungeeCoreSystem);
    }
}

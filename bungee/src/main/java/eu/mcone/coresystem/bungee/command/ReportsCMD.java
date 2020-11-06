package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportsCMD extends CorePlayerCommand {

    public ReportsCMD() {
        super("reports");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "report list");
    }

}

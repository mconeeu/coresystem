package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class JoinCMD extends CorePlayerCommand {

    public JoinCMD() {
        super("join");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 1) {
            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);

            if (t != null) {
                if (JoinMeCMD.hasValidJoinMe(t.getUniqueId())) {
                    if (!p.getServer().getInfo().equals(t.getServer().getInfo())) {
                        p.connect(t.getServer().getInfo());
                        BungeeCoreSystem.getSystem().getMessenger().sendSuccess(p, "Du hast du Einladung von "+t.getName()+" für den Server !["+t.getServer().getInfo().getName()+"] angenommen!");
                    } else {
                        BungeeCoreSystem.getSystem().getMessenger().sendWarning(p, "Du bist bereits auf diesem Server!");
                    }
                } else {
                    BungeeCoreSystem.getSystem().getMessenger().sendError(p, "Diese Einladung ist abgelaufen!");
                }
            } else {
                BungeeCoreSystem.getSystem().getMessenger().sendError(p, "Der Spieler "+args[0]+" ist nicht online.");
            }
            return;
        }

        BungeeCoreSystem.getSystem().getMessenger().sendError(p, "Bitte benutze: ![/join <name>]");
    }

}

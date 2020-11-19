package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class NameCMD extends CoreCommand {

    public NameCMD() {
        super("name", "system.bungee.nick.check");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, "nick check "+args[0]);
        } else {
            BungeeCoreSystem.getSystem().getMessenger().sendSender(sender, "Bitte benutze: ![/name <name>]");
        }
    }

}

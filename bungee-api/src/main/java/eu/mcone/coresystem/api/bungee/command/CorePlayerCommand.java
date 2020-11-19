package eu.mcone.coresystem.api.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class CorePlayerCommand extends CoreCommand {

    public CorePlayerCommand(String name) {
        super(name);
    }

    public CorePlayerCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            onPlayerCommand((ProxiedPlayer) sender, args);
        } else {
            CoreSystem.getInstance().getMessenger().sendSenderTransl(sender, "system.command.consolesender");
        }
    }

    public abstract void onPlayerCommand(ProxiedPlayer p, String[] args);

}
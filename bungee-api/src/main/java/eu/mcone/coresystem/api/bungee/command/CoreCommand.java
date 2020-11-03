package eu.mcone.coresystem.api.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class CoreCommand extends Command {

    public CoreCommand(String name) {
        super(name);
    }

    public CoreCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), ((ProxiedPlayer) sender).getUniqueId()))
            return;

        onCommand(sender, args);
    }

    public abstract void onCommand(CommandSender sender, String[] args);

}

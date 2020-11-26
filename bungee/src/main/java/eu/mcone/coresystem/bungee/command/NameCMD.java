package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class NameCMD extends CoreCommand implements TabExecutor {

    public NameCMD() {
        super("name", "system.bungee.nick.check");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, "nick check "+args[0]);
        } else {
            BungeeCoreSystem.getSystem().getMessenger().send(sender, "Bitte benutze: ![/name <name>]");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            Set<String> matches = new HashSet<>();

            for (Nick nick : CoreSystem.getInstance().getNickManager().getPlayerNicks().keySet()) {
                if (nick.getName().startsWith(search)) {
                    matches.add(nick.getName());
                }
            }

            return matches;
        }

        return ImmutableSet.of();
    }

}

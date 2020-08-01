package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class OverwatchCMD extends Command {

    private final Overwatch overwatch;

    public OverwatchCMD(Overwatch overwatch) {
        super("overwatch", "overwatch.login");
        this.overwatch = overwatch;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("login")) {
                    if (overwatch.isLoggedIn(player)) {
                        overwatch.getMessenger().send(player, "§cDu bist bereits eingeloggt!");
                    } else {
                        overwatch.login(player);
                        overwatch.getMessenger().send(player, "§aDu hast dich erfolgreich eingeloggt.");
                    }

                    return;
                } else if (args[0].equalsIgnoreCase("logout")) {
                    if (!overwatch.isLoggedIn(player)) {
                        overwatch.getMessenger().send(player, "§cDu bist nicht eingelogt!");
                    } else {
                        if ((overwatch.getLoggedIn().size() - 1) > 0) {
                            overwatch.logout(player);
                            overwatch.getMessenger().send(player, "§aDu hast dich erfolgreich ausgelogt.");
                        } else {
                            overwatch.getMessenger().send(player, "§cDu kannst dich nicht ausloggen da außer dir niemand eingelogt ist!");
                        }
                    }

                    return;
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (overwatch.isLoggedIn(player)) {
                        if ((overwatch.getLoggedIn().size() - 1) <= 0) {
                            overwatch.getMessenger().send(player, "§cAußer dir ist momentan niemand eingeloggt.");
                            return;
                        }
                    }

                    overwatch.getMessenger().send(player, "§7Folgende(r) Spieler ist/sind momentan eingelogt:");
                    for (ProxiedPlayer loggedIn : overwatch.getLoggedIn()) {
                        if (loggedIn != player) {
                            CorePlayer corePlayer = BungeeCoreSystem.getSystem().getCorePlayer(loggedIn);
                            overwatch.getMessenger().send(player, "§8» §f" + corePlayer.getName() + " §7Status: §f" + corePlayer.getState().getName());
                        }
                    }

                    return;
                }
            }

            overwatch.getMessenger().send(player, "§4Bitte benutze: " +
                    "\n§c/overwatch login §4oder " +
                    "\n§c/overwatch logout §4oder " +
                    "\n§c/overwatch list"
            );
        }
    }
}

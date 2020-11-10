package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OverwatchCMD extends CorePlayerCommand {

    private final Overwatch overwatch;

    public OverwatchCMD(Overwatch overwatch) {
        super("overwatch", "system.bungee.overwatch.report");
        this.overwatch = overwatch;
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("login")) {
                if (overwatch.isLoggedIn(p)) {
                    overwatch.getMessenger().send(p, "§cDu bist bereits eingeloggt!");
                } else {
                    overwatch.login(p);
                    overwatch.getMessenger().send(p, "§aDu hast dich erfolgreich eingeloggt.");
                }

                return;
            } else if (args[0].equalsIgnoreCase("logout")) {
                if (!overwatch.isLoggedIn(p)) {
                    overwatch.getMessenger().send(p, "§cDu bist nicht eingelogt!");
                } else {
                    if ((overwatch.getLoggedIn().size() - 1) > 0) {
                        overwatch.logout(p);
                        overwatch.getMessenger().send(p, "§aDu hast dich erfolgreich ausgelogt.");
                    } else {
                        overwatch.getMessenger().send(p, "§cDu kannst dich nicht ausloggen da außer dir niemand eingelogt ist!");
                    }
                }

                return;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (overwatch.isLoggedIn(p)) {
                    if ((overwatch.getLoggedIn().size() - 1) <= 0) {
                        overwatch.getMessenger().send(p, "§cAußer dir ist momentan niemand eingeloggt.");
                        return;
                    }
                }

                overwatch.getMessenger().send(p, "§7Folgende(r) Spieler ist/sind momentan eingelogt:");
                for (ProxiedPlayer loggedIn : overwatch.getLoggedIn()) {
                    if (loggedIn != p) {
                        CorePlayer corePlayer = BungeeCoreSystem.getSystem().getCorePlayer(loggedIn);
                        overwatch.getMessenger().send(p, "§8» §f" + corePlayer.getName() + " §7Status: §f" + corePlayer.getState().getName());
                    }
                }

                return;
            }
        }

        overwatch.getMessenger().send(p, "§4Bitte benutze: " +
                "\n§c/overwatch login §4oder " +
                "\n§c/overwatch logout §4oder " +
                "\n§c/overwatch list"
        );
    }
}

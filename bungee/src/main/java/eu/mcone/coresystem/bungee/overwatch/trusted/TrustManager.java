package eu.mcone.coresystem.bungee.overwatch.trusted;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.trust.TrustGroup;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;

import java.util.UUID;

public class TrustManager {

    private final Overwatch overwatch;

    public TrustManager(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    public void checkTrustLvl(UUID player) {
        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
        int correct = 0;
        int reports = 0;
        if (corePlayer != null) {
            correct = corePlayer.getTrust().getReports() / 100 * corePlayer.getTrust().getCorrectReports();
            reports = corePlayer.getTrust().getReports();
        } else {
            try {
                OfflineCorePlayer offlineCorePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(player);
                correct = offlineCorePlayer.getTrust().getReports() / 100 * offlineCorePlayer.getTrust().getCorrectReports();
                reports = offlineCorePlayer.getTrust().getReports();
            } catch (PlayerNotResolvedException e) {
                e.printStackTrace();
            }
        }

        if (reports > 10) {
            for (TrustGroup group : TrustGroup.values()) {
                if (correct > group.getMin() && correct < group.getMax()) {
                    if (corePlayer != null) {
                        if (corePlayer.getTrust().getGroup() != group) {
                            if (corePlayer.getTrust().getGroup().getRank() > group.getRank()) {
                                overwatch.getMessenger().send(corePlayer.bungee(), "§7Dein Trustgruppe wurde §cheruntergesetzt§7, aktuelle Trustgruppe: " + group.getPrefix());
                            } else {
                                overwatch.getMessenger().send(corePlayer.bungee(), "§7Dein Trustgruppe wurde §ahochgestuft§7, aktuelle Trustgruppe: " + group.getPrefix());
                            }
                        }
                    }

                    break;
                }
            }
        }
    }
}

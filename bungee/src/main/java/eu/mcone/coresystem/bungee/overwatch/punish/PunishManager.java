package eu.mcone.coresystem.bungee.overwatch.punish;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.punish.PunishTemplate;
import eu.mcone.coresystem.api.core.overwatch.report.AbstractReport;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class PunishManager implements eu.mcone.coresystem.api.bungee.overwatch.punish.PunishManager {

    private final Overwatch overwatch;
    @Getter
    private final MongoCollection<Punish> punishCollection;
    @Getter
    private final MongoCollection<Document> punishPointsCollection;
    @Getter
    private final MongoCollection<Punish> punishHistoryCollection;

    public PunishManager(Overwatch overwatch) {
        this.overwatch = overwatch;

        this.punishCollection = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishments", Punish.class);
        this.punishPointsCollection = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishment_points");
        this.punishHistoryCollection = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishment_history", Punish.class);
    }

    public void punishPlayer(AbstractReport report, UUID teamMember) {
        try {
            OfflineCorePlayer reportedCorePlayer = CoreSystem.getInstance().getOfflineCorePlayer(report.getReported());

            long millis = System.currentTimeMillis() / 1000;
            PunishTemplate template = report.getReportReason().getTemplate();
            String templateName = template.getName();
            addPoints(reportedCorePlayer.getUuid(), template.getBanPoints(), template.getMutePoints());

            Map<String, Integer> points = getPoints(reportedCorePlayer.getUuid());
            int banpoints = points.get("ban");
            int mutepoints = points.get("mute");

            long banTime = millis + getBanTimestampByPoints(banpoints);
            long muteTime = millis + getMuteTimestampByPoints(mutepoints);


            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(reportedCorePlayer.getUuid());
            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(teamMember);
            String tName;

            if (t != null) {
                tName = t.getName();
            } else {
                tName = "System";
            }

            Punish punish = new Punish(reportedCorePlayer.getUuid(), teamMember, template, report.getReportReason().getName());
            if (banTime > millis && template.getBanPoints() > 0) {
                punish.addBanEntry(banTime);
                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", reportedCorePlayer.getUuid()), set("state", 3));

                if (p != null) {
                    p.disconnect(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
                            + "\n§7§oDu wurdest vom Netzwerk gebannt"
                            + "\n§r"
                            + "\n§7Gebannt von §8» §e" + tName
                            + "\n§7Grund §8» §c" + templateName + " §7/§c " + report.getReportReason().getName()
                            + "\n§7Gebannt für §8» " + getEndeString(banTime)
                            + "\n§r"
                            + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                            + "\n§7TS-Server §8» §fts.mcone.eu"
                            + "\n§7Homepage §8» §fwww.mcone.eu/unban")));
                }

                overwatch.getMessenger().send(t, "§7Du hast den Spieler §aerfolgreich bestraft §8(§7Gebannt für §e" + getEndeString(banTime) + "§8)");
            }

            if (muteTime > millis && template.getMutePoints() > 0) {
                punish.addMuteEntry(muteTime, "");

                if (p != null) {
                    BungeeCorePlayer cp = ((BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(p));
                    cp.setMuteTime(muteTime);
                    cp.setMuted(true);

                    overwatch.getMessenger().sendSimple(p, "\n§8§m----------------§r§8 §eOverwatch §8§m-----------------"
                            + "\n§f§lMC ONE §3Minecraftnetzwerk"
                            + "\n§7§oDu wurdest gemuted"
                            + "\n§r"
                            + "\n§7Gemuted von §8» §e" + tName
                            + "\n§7Grund §8» §c" + template.getName() + " §7/§c " + report.getReportReason().getName()
                            + "\n§7Gemutet für §8» " + getEndeString(muteTime)
                            + "\n§r"
                            + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                            + "\n§7TS-Server §8» §fts.mcone.eu"
                            + "\n§7Homepage §8» §fhttps://www.mcone.eu/unban"
                            + "\n§8§m----------------------------------------\n");

                    overwatch.getMessenger().send(t, "§7Du hast den Spieler §aerfolgreich bestraft §8(§7Gemutet für §e" + getEndeString(muteTime) + "§8)");
                }
            }

            OfflineCorePlayer trustedUser;
            for (UUID uuid : report.getReporter()) {
                trustedUser = BungeeCoreSystem.getSystem().getOfflineCorePlayer(uuid);
                trustedUser.increaseCorrectReports();
                overwatch.getTrustManager().checkTrustLvl(uuid);
            }

            if (punish.getBanEntry() != null || punish.getMuteEntry() != null) {
                punishCollection.replaceOne(eq("punished", reportedCorePlayer.getUuid()), punish, new ReplaceOptions().upsert(true));
            }
        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }
    }

    public void punishPlayer(UUID player, PunishTemplate template, String reason, UUID teamMember) {
        CoreSystem.getInstance().sendConsoleMessage("Banning user with uuid \"" + player.toString() + "\" with template \"" + template.getName() + "\" with reason \"" + reason + "\" by team member with uuid \"" + teamMember.toString() + "\"");
        long millis = System.currentTimeMillis() / 1000;

        String templateName = template.getName();
        addPoints(player, template.getBanPoints(), template.getMutePoints());

        Map<String, Integer> points = getPoints(player);
        int banpoints = points.get("ban");
        int mutepoints = points.get("mute");

        long banTime = millis + getBanTimestampByPoints(banpoints);
        long muteTime = millis + getMuteTimestampByPoints(mutepoints);


        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
        ProxiedPlayer t = ProxyServer.getInstance().getPlayer(teamMember);
        String tName;

        if (t != null) {
            tName = t.getName();
        } else {
            tName = "System";
        }

        Punish punish = new Punish(player, teamMember, template, reason);
        if (banTime > millis && template.getBanPoints() > 0) {
            punish.addBanEntry(banTime);
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", player), set("state", 3));

            if (p != null) {
                p.disconnect(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
                        + "\n§7§oDu wurdest vom Netzwerk gebannt"
                        + "\n§r"
                        + "\n§7Gebannt von §8» §e" + tName
                        + "\n§7Grund §8» §c" + templateName + " §7/§c " + reason
                        + "\n§7Gebannt für §8» " + getEndeString(banTime)
                        + "\n§r"
                        + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                        + "\n§7TS-Server §8» §fts.mcone.eu"
                        + "\n§7Homepage §8» §fwww.mcone.eu/unban")));
            }
        }

        if (muteTime > millis && template.getMutePoints() > 0) {
            punish.addMuteEntry(muteTime, "TEST");

            if (p != null) {
                BungeeCorePlayer cp = ((BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(p));
                cp.setMuteTime(muteTime);
                cp.setMuted(true);

                overwatch.getMessenger().sendSimple(p, "\n§8§m----------------§r§8 §eOverwatch §8§m-----------------"
                        + "\n§f§lMC ONE §3Minecraftnetzwerk"
                        + "\n§7§oDu wurdest gemuted"
                        + "\n§r"
                        + "\n§7Gemuted von §8» §e" + tName
                        + "\n§7Grund §8» §c" + template.getName() + " §7/§c " + reason
                        + "\n§7Gemutet für §8» " + getEndeString(muteTime)
                        + "\n§r"
                        + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                        + "\n§7TS-Server §8» §fts.mcone.eu"
                        + "\n§7Homepage §8» §fhttps://www.mcone.eu/unban"
                        + "\n§8§m----------------------------------------\n");
            }
        }

        if (punish.getBanEntry() != null || punish.getMuteEntry() != null) {
            punishCollection.replaceOne(eq("punished", player), punish, new ReplaceOptions().upsert(true));
        }
    }

    public void resetPoints(UUID uuid) {
        punishPointsCollection.updateOne(eq("uuid", uuid), combine(
                new Document("banpoints", 0),
                new Document("mutepoints", 0)
        ));
    }

    public void unPunish(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isMuted()) {
                BungeeCorePlayer p = (BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(uuid);
                if (p != null) {
                    p.setMuted(false);
                    p.setMuteTime(0);
                }
            }

            punishCollection.deleteOne(eq("punished", uuid));
            punishHistoryCollection.insertOne(punish);
        }
    }

    public void unBan(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isBanned()) {
                if (punish.getBanEntry() != null && punish.getMuteEntry() != null) {
                    punish.unMute();
                    punish.unBan();
                    punishCollection.deleteOne(eq("punished", uuid));
                } else if (punish.getBanEntry() != null) {
                    punish.unBan();
                }

                punishCollection.deleteOne(eq("punished", uuid));
                punishHistoryCollection.insertOne(punish);
            }
        }
    }

    public void unMute(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isMuted()) {
                if (punish.getBanEntry() != null && punish.getMuteEntry() != null) {
                    punish.unBan();
                    punish.unMute();
                } else if (punish.getMuteEntry() != null) {
                    punish.unMute();
                }

                BungeeCorePlayer p = (BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(uuid);
                if (p != null) {
                    p.setMuted(false);
                    p.setMuteTime(0);
                }

                punishCollection.deleteOne(eq("punished", uuid));
                punishHistoryCollection.insertOne(punish);
            }
        }
    }

    public boolean isBanned(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isBanned()) {
                if (punish.getBanEntry().getUnPunished() == 0 && punish.getBanEntry().getEnd() <= (System.currentTimeMillis() / 1000)) {
                    unBan(uuid);
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isMuted(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isMuted()) {
                if (punish.getMuteEntry().getUnPunished() == 0 && punish.getMuteEntry().getEnd() <= (System.currentTimeMillis() / 1000)) {
                    unMute(uuid);
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }

    public Punish getPunish(UUID uuid) {
        return punishCollection.find(eq("punished", uuid)).first();
    }

    public boolean hasPoints(UUID uuid) {
        return punishPointsCollection.find(eq("uuid", uuid)).first() != null;
    }

    public Map<String, Integer> getPoints(UUID uuid) {
        Map<String, Integer> result = new HashMap<>();
        Document entry = punishPointsCollection.find(eq("uuid", uuid)).first();
        if (entry != null) {
            result.put("ban", entry.getInteger("banpoints"));
            result.put("mute", entry.getInteger("mutepoints"));
        }

        return result;
    }

    private void addPoints(UUID uuid, int banpoints, int mutepoints) {
        if (hasPoints(uuid)) {
            punishPointsCollection.updateOne(
                    eq("uuid", uuid),
                    combine(
                            inc("banpoints", banpoints),
                            inc("mutepoints", mutepoints)
                    )
            );
        } else {
            punishPointsCollection.insertOne(
                    new Document("uuid", uuid)
                            .append("banpoints", banpoints)
                            .append("mutepoints", mutepoints)
            );
        }
    }

    public long getBanTimestampByPoints(int points) {
        long result;

        if (points >= 20) {
            result = 60 * 60 * 24 * 365 * 10; //Permanent
        } else if (points >= 15) {
            result = 60 * 60 * 24 * 60; //60 Tage
        } else if (points >= 10) {
            result = 60 * 60 * 24 * 30; //30 Tage
        } else if (points >= 5) {
            result = 60 * 60 * 24 * 7; //7 Tage
        } else if (points >= 2) {
            result = 60 * 60 * 24; //24 Stunden
        } else if (points >= 1) {
            result = 60 * 60 * 12; //12 Stunden
        } else {
            result = 0;
        }

        return result;
    }

    public long getMuteTimestampByPoints(int points) {
        long result;

        if (points >= 20) {
            result = 60 * 60 * 24 * 365 * 10; //Permanent
        } else if (points >= 15) {
            result = 60 * 60 * 24 * 70; //70 Tage
        } else if (points >= 10) {
            result = 60 * 60 * 24 * 35; //35 Tage
        } else if (points >= 5) {
            result = 60 * 60 * 24 * 10; //10 Tage
        } else if (points >= 2) {
            result = 60 * 60 * 36; //36 Stunden
        } else if (points >= 1) {
            result = 60 * 60 * 18; //18 Stunden
        } else {
            result = 0;
        }

        return result;
    }

    public String getEndeString(long end) {
        long millis = System.currentTimeMillis() / 1000;

        if (end > 60 * 60 * 24 * 365 + millis) {
            return "§eimmer";
        } else {
            long seconds = end - millis, minutes = 0, hours = 0, days = 0;

            while (seconds > 60) {
                seconds -= 60;
                minutes++;
            }

            while (minutes > 60) {
                minutes -= 60;
                hours++;
            }

            while (hours > 24) {
                hours -= 24;
                days++;
            }

            return "§e" + days + " §7Tag(e), §e" + hours + " §7Stunde(n) und §e" + ++minutes + " §7Minute(n)";
        }
    }
}

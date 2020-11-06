package eu.mcone.coresystem.bungee.overwatch.punish;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.punish.PunishTemplate;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.player.BungeeOfflineCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class PunishManager implements eu.mcone.coresystem.api.bungee.overwatch.punish.PunishManager {

    private final Overwatch overwatch;
    @Getter
    public static final MongoCollection<Punish> PUNISH_COLLECTION = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishments", Punish.class);
    @Getter
    public static final MongoCollection<Document> PUNISH_POINTS_COLLECTION = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishment_points");
    @Getter
    public static final MongoCollection<Punish> PUNISH_HISTORY_COLLECTION = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("overwatch_punishment_history", Punish.class);

    public PunishManager(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    public void punishPlayer(Report report, UUID member) {
        try {
            OfflineCorePlayer reportedCorePlayer = CoreSystem.getInstance().getOfflineCorePlayer(report.getReported());
            PunishTemplate template = report.getReason().getTemplate();
            Punish punish = new Punish(reportedCorePlayer.getUuid(), member, template, report.getReason().getName());
            report.setPunishID(punish.getPunishID());
            report.addUpdate(reportedCorePlayer.getName() + " wurde mit dem template " + template.getName() + " bestraft.");
            report.setState(ReportState.CLOSED);
            overwatch.getReportManager().updateDBEntry(report);
            punish(punish, report.getReported(), member, template, report.getReason().getName(), report.getReplayID(), null, report.getReporter());
        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }
    }

    public void punishPlayer(UUID target, PunishTemplate template, String reason, String chatLogID, UUID member) {
        CoreSystem.getInstance().sendConsoleMessage("Banning user with uuid \"" + target.toString() + "\" with template \"" + template.getName() + "\" with reason \"" + reason + "\" by team member with uuid \"" + member.toString() + "\"");
        punish(new Punish(target, member, template, reason), target, member, template, reason, null, chatLogID, null);
    }

    private void punish(Punish punish, UUID target, UUID member, PunishTemplate template, String reason, String replayID, String chatLogID, List<UUID> reporter) {
        List<PunishTemplate.PunishTyp> types = template.getTypes();

        if (types.size() != 0) {
            long millis = System.currentTimeMillis() / 1000;

            addPoints(target, template.getBanPoints(), template.getMutePoints());
            Map<String, Integer> points = getPoints(target);

            BungeeCorePlayer targetBungeePlayer = (BungeeCorePlayer) BungeeCoreSystem.getSystem().getCorePlayer(target);
            ProxiedPlayer memberBungeePlayer = ProxyServer.getInstance().getPlayer(member);

            String memberName;
            String targetName = target.toString();

            if (memberBungeePlayer != null) {
                memberName = memberBungeePlayer.getName();
            } else {
                memberName = "System";
            }

            if (types.contains(PunishTemplate.PunishTyp.BAN)) {
                long banTime = millis + getBanTimestampByPoints(points.get("ban"));

                if (banTime > millis) {
                    punish.addBanEntry(banTime, replayID);
                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", target), set("state", 3));

                    if (targetBungeePlayer != null) {
                        targetName = targetBungeePlayer.getName();
                        targetBungeePlayer.bungee().disconnect(TextComponent.fromLegacyText(
                                "§f§lMC ONE §3Minecraftnetzwerk"
                                        + "\n§7§oDu wurdest vom Netzwerk gebannt"
                                        + "\n§r"
                                        + "\n§7Gebannt von §8» §e" + memberName
                                        + "\n§7Grund §8» §c" + template.getName() + " §7/§c " + reason
                                        + "\n§7Gebannt für §8» " + getEndeString(banTime)
                                        + (replayID != null ? "\n§7RpelayID: " + replayID : "")
                                        + "\n§r"
                                        + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                                        + "\n§7TS-Server §8» §fts.mcone.eu"
                                        + "\n§7Homepage §8» §fwww.mcone.eu/unban"
                        ));
                    } else {
                        try {
                            BungeeOfflineCorePlayer offlineCorePlayer = (BungeeOfflineCorePlayer) BungeeCoreSystem.getSystem().getOfflineCorePlayer(target);
                            targetName = offlineCorePlayer.getName();

                            offlineCorePlayer.setBanned(true);
                            offlineCorePlayer.setBanTime(banTime);
                            offlineCorePlayer.updatePunishmentAsync();
                        } catch (PlayerNotResolvedException e) {
                            e.printStackTrace();
                        }
                    }

                    overwatch.getMessenger().send(memberBungeePlayer, "§7Du hast den Spieler §aerfolgreich bestraft §8(§7Gebannt für §e" + getEndeString(banTime) + "§8)");
                }
            }

            if (types.contains(PunishTemplate.PunishTyp.MUTE)) {
                long muteTime = millis + getMuteTimestampByPoints(points.get("mute"));

                if (muteTime > millis) {
                    punish.addMuteEntry(muteTime, chatLogID);

                    if (targetBungeePlayer != null) {
                        targetName = targetBungeePlayer.getName();
                        targetBungeePlayer.setMuteTime(muteTime);
                        targetBungeePlayer.setMuted(true);

                        overwatch.getMessenger().sendSimple(targetBungeePlayer.bungee(), "\n§8§m----------------§r§8 §eOverwatch §8§m-----------------"
                                + "\n§f§lMC ONE §3Minecraftnetzwerk"
                                + "\n§7§oDu wurdest gemuted"
                                + "\n§r"
                                + "\n§7Gemuted von §8» §e" + memberName
                                + "\n§7Grund §8» §c" + template.getName() + " §7/§c " + reason
                                + "\n§7Gemutet für §8» " + getEndeString(muteTime)
                                + (chatLogID != null ? "\n§7ChatLogID: " + chatLogID : "")
                                + "\n§r"
                                + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                                + "\n§7TS-Server §8» §fts.mcone.eu"
                                + "\n§7Homepage §8» §fhttps://www.mcone.eu/unban"
                                + "\n§8§m----------------------------------------\n");

                        overwatch.getMessenger().send(memberBungeePlayer, "§7Du hast den Spieler §aerfolgreich bestraft §8(§7Gemutet für §e" + getEndeString(muteTime) + "§8)");
                    } else {
                        try {
                            BungeeOfflineCorePlayer offlineCorePlayer = (BungeeOfflineCorePlayer) BungeeCoreSystem.getSystem().getOfflineCorePlayer(target);
                            targetName = offlineCorePlayer.getName();

                            offlineCorePlayer.setMuteTime(muteTime);
                            offlineCorePlayer.setMuted(true);
                            offlineCorePlayer.updatePunishmentAsync();
                        } catch (PlayerNotResolvedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            String action;
            if (types.contains(PunishTemplate.PunishTyp.BAN) && types.contains(PunishTemplate.PunishTyp.MUTE)) {
                action = "gebannt & gemutet";
            } else {
                if (types.contains(PunishTemplate.PunishTyp.BAN)) {
                    action = "gebannt";
                } else {
                    action = "gemutet";
                }
            }

            for (ProxiedPlayer player : overwatch.getLoggedIn()) {
                overwatch.getMessenger().send(player, "§a" + memberName + " §7hat den Spieler §c" + targetName + " §7für §f" + template.getName() + " §7" + action + ".");
            }

            if (reporter != null) {
                OfflineCorePlayer trustedUser;
                for (UUID uuid : reporter) {
                    try {
                        trustedUser = BungeeCoreSystem.getSystem().getOfflineCorePlayer(uuid);
                        trustedUser.increaseCorrectReports();
                        overwatch.getTrustManager().checkTrustLvl(uuid);
                    } catch (PlayerNotResolvedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (memberBungeePlayer != null) {
                BungeeCoreSystem.getSystem().getChannelHandler().createInfoRequest(memberBungeePlayer, "INVENTORY", "CLOSE");
            }

            if (punish.getBanEntry() != null || punish.getMuteEntry() != null) {
                System.out.println("INSERT");
                PUNISH_COLLECTION.replaceOne(eq("punished", target), punish, new ReplaceOptions().upsert(true));
            }
        } else {
            BungeeCoreSystem.getSystem().sendConsoleMessage("§4Could find punish typ for template " + template.getName());
        }
    }

    public void resetPoints(UUID uuid) {
        PUNISH_POINTS_COLLECTION.updateOne(eq("uuid", uuid), combine(
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

            PUNISH_COLLECTION.deleteOne(eq("punished", uuid));
            PUNISH_HISTORY_COLLECTION.insertOne(punish);
        }
    }

    public void unBan(UUID uuid) {
        Punish punish = getPunish(uuid);

        if (punish != null) {
            if (punish.isBanned()) {
                if (punish.getBanEntry() != null && punish.getMuteEntry() != null) {
                    punish.unMute();
                    punish.unBan();
                    PUNISH_COLLECTION.deleteOne(eq("punished", uuid));
                } else if (punish.getBanEntry() != null) {
                    punish.unBan();
                }

                PUNISH_COLLECTION.deleteOne(eq("punished", uuid));
                PUNISH_HISTORY_COLLECTION.insertOne(punish);
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

                PUNISH_COLLECTION.deleteOne(eq("punished", uuid));
                PUNISH_HISTORY_COLLECTION.insertOne(punish);
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
        return PUNISH_COLLECTION.find(eq("punished", uuid)).first();
    }

    public boolean hasPoints(UUID uuid) {
        return PUNISH_POINTS_COLLECTION.find(eq("uuid", uuid)).first() != null;
    }

    public Map<String, Integer> getPoints(UUID uuid) {
        Map<String, Integer> result = new HashMap<>();
        Document entry = PUNISH_POINTS_COLLECTION.find(eq("uuid", uuid)).first();
        if (entry != null) {
            result.put("ban", entry.getInteger("banpoints"));
            result.put("mute", entry.getInteger("mutepoints"));
        }

        return result;
    }

    private void addPoints(UUID uuid, int banpoints, int mutepoints) {
        if (hasPoints(uuid)) {
            PUNISH_POINTS_COLLECTION.updateOne(
                    eq("uuid", uuid),
                    combine(
                            inc("banpoints", banpoints),
                            inc("mutepoints", mutepoints)
                    )
            );
        } else {
            PUNISH_POINTS_COLLECTION.insertOne(
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

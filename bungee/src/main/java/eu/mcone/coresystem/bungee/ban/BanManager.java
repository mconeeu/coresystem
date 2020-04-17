/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.ban;

import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class BanManager {

    public static void ban(UUID gebannt, BanTemplate template, String grund, UUID team_member) {
        CoreSystem.getInstance().sendConsoleMessage("Banning user with uuid \"" + gebannt.toString() + "\" with template \"" + template.getName() + "\" with reason \"" + grund + "\" by team member with uuid \"" + team_member.toString() + "\"");
        long millis = System.currentTimeMillis() / 1000;

        String templateName = template.getName();
        addPoints(gebannt, template.getBanPoints(), template.getMutePoints());

        Map<String, Integer> points = getPoints(gebannt);
        int banpoints = points.get("ban");
        int mutepoints = points.get("mute");

        long banTime = millis + getBanTimestampByPoints(banpoints);
        long muteTime = millis + getMuteTimestampByPoints(mutepoints);


        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(gebannt);
        ProxiedPlayer t = ProxyServer.getInstance().getPlayer(team_member);
        String tName;

        if (t != null) {
            tName = t.getName();
        } else {
            tName = "System";
        }

        if (banTime > millis && template.getBanPoints() > 0) {
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_ban").updateOne(
                    eq("uuid", gebannt.toString()),
                    combine(
                            set("uuid", gebannt.toString()),
                            set("template", templateName),
                            set("reason", grund),
                            set("end", banTime),
                            set("timestamp", millis),
                            set("team_member", tName)
                    ),
                    new UpdateOptions().upsert(true)
            );
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_banhistory").insertOne(
                    new Document("uuid", gebannt.toString())
                            .append("template", templateName)
                            .append("reason", grund)
                            .append("end", banTime)
                            .append("timestamp", millis)
                            .append("team_member", tName)
            );
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", gebannt.toString()), set("state", 3));

            if (p != null) {
                p.disconnect(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
                        + "\n§7§oDu wurdest vom Netzwerk gebannt"
                        + "\n§r"
                        + "\n§7Gebannt von §8» §e" + tName
                        + "\n§7Grund §8» §c" + templateName + " §7/§c " + grund
                        + "\n§7Gebannt für §8» " + getEndeString(banTime)
                        + "\n§r"
                        + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                        + "\n§7TS-Server §8» §fts.mcone.eu"
                        + "\n§7Homepage §8» §fwww.mcone.eu/unban")));
            }
        }

        if (muteTime > millis && template.getMutePoints() > 0) {
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").updateOne(
                    eq("uuid", gebannt.toString()),
                    combine(
                            set("uuid", gebannt.toString()),
                            set("template", templateName),
                            set("reason", grund),
                            set("end", muteTime),
                            set("timestamp", millis),
                            set("team_member", tName)
                    ),
                    new UpdateOptions().upsert(true)
            );
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mutehistory").insertOne(
                    new Document("uuid", gebannt.toString())
                            .append("template", templateName)
                            .append("reason", grund)
                            .append("end", muteTime)
                            .append("timestamp", millis)
                            .append("team_member", tName)
            );

            if (p != null) {
                BungeeCorePlayer cp = ((BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(p));
                cp.setMuteTime(muteTime);
                cp.setMuted(true);

                BungeeCoreSystem.getInstance().getMessenger().sendSimple(p, "\n§8§m----------------§r§8 [§7§l!§8] §fSystem §8§m----------------"
                        + "\n§f§lMC ONE §3Minecraftnetzwerk"
                        + "\n§7§oDu wurdest gemuted"
                        + "\n§r"
                        + "\n§7Gemuted von §8» §e" + tName
                        + "\n§7Grund §8» §c" + template.getName() + " §7/§c " + grund
                        + "\n§7Gemutet für §8» " + getEndeString(muteTime)
                        + "\n§r"
                        + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                        + "\n§7TS-Server §8» §fts.mcone.eu"
                        + "\n§7Homepage §8» §fhttps://www.mcone.eu/unban"
                        + "\n§8§m----------------------------------------\n");
            }
        }
    }

    public static void unban(UUID uuid) {
        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_ban").deleteOne(eq("uuid", uuid.toString()));
    }

    public static void unmute(UUID uuid) {
        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").deleteOne(eq("uuid", uuid.toString()));

        BungeeCorePlayer p = (BungeeCorePlayer) CoreSystem.getInstance().getCorePlayer(uuid);
        if (p != null) {
            p.setMuted(false);
            p.setMuteTime(0);
        }
    }

    public static Document getBanEntry(UUID uuid) {
        Document result;
        if ((result = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_ban").find(eq("uuid", uuid.toString())).first()) != null) {
            if (result.getLong("end") <= (System.currentTimeMillis() / 1000)) {
                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_ban").deleteOne(eq(result.get("_id")));
                return null;
            } else {
                return result;
            }
        } else {
            return null;
        }
    }

    public static Document getMuteEntry(UUID uuid) {
        Document result;
        if ((result = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").find(eq("uuid", uuid.toString())).first()) != null) {
            if (result.getLong("end") <= (System.currentTimeMillis() / 1000)) {
                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").deleteOne(eq(result.get("_id")));
                return null;
            } else {
                return result;
            }
        } else {
            return null;
        }
    }

    public static boolean isBanned(UUID uuid) {
        return getBanEntry(uuid) != null;
    }

    public static boolean isMuted(UUID uuid) {
        return getMuteEntry(uuid) != null;
    }

    private static boolean hasPoints(UUID uuid) {
        return BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_points").find(eq("uuid", uuid.toString())).first() != null;
    }

    private static Map<String, Integer> getPoints(UUID uuid) {
        Map<String, Integer> result = new HashMap<>();

        Document entry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_points").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            result.put("ban", entry.getInteger("banpoints"));
            result.put("mute", entry.getInteger("mutepoints"));
        }

        return result;
    }

    private static void addPoints(UUID uuid, int banpoints, int mutepoints) {
        if (hasPoints(uuid)) {
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_points").updateOne(
                    eq("uuid", uuid.toString()),
                    combine(
                            inc("banpoints", banpoints),
                            inc("mutepoints", mutepoints)
                    )
            );
        } else {
            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_points").insertOne(
                    new Document("uuid", uuid.toString())
                            .append("banpoints", banpoints)
                            .append("mutepoints", mutepoints)
            );
        }
    }

    private static long getBanTimestampByPoints(int points) {
        long result;

        if (points >= 20) {
            result = 60 * 60 * 24 * 365 * 10;
        } else if (points >= 15) {
            result = 60 * 60 * 24 * 60;
        } else if (points >= 10) {
            result = 60 * 60 * 24 * 30;
        } else if (points >= 5) {
            result = 60 * 60 * 24 * 7;
        } else if (points >= 2) {
            result = 60 * 60 * 24;
        } else if (points >= 1) {
            result = 60 * 60 * 12;
        } else {
            result = 0;
        }

        return result;
    }

    private static long getMuteTimestampByPoints(int points) {
        long result;

        if (points >= 20) {
            result = 60 * 60 * 24 * 365 * 10;
        } else if (points >= 15) {
            result = 60 * 60 * 24 * 60;
        } else if (points >= 10) {
            result = 60 * 60 * 24 * 30;
        } else if (points >= 5) {
            result = 60 * 60 * 24 * 14;
        } else if (points >= 2) {
            result = 60 * 60 * 24 * 2;
        } else if (points >= 1) {
            result = 60 * 60 * 24;
        } else {
            result = 0;
        }

        return result;
    }

    public static String getEndeString(long end) {
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

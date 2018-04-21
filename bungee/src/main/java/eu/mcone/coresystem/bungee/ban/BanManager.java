/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.ban;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanManager {

	public static void ban(UUID gebannt, BanTemplate template, String grund, UUID team_member){
        System.out.println("Banning user with uuid \""+gebannt.toString()+"\" with template \""+template.getName()+"\" with reason \""+grund+"\" by team member with uuid \""+team_member.toString()+"\"");
        long millis = System.currentTimeMillis() / 1000;

	    String templateName = template.getName();
	    addPoints(gebannt, template.getBanPoints(), template.getMutePoints());

	    Map<String, Integer> points = getPoints(gebannt);
	    int banpoints = points.get("ban");
	    int mutepoints = points.get("mute");

        long banTime = millis+getBanTimestampByPoints(banpoints);
	    long muteTime = millis+getMuteTimestampByPoints(mutepoints);


        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(gebannt);
        ProxiedPlayer t = ProxyServer.getInstance().getPlayer(team_member);
        String tName;

        if (t != null) {
            tName = t.getName();
        } else {
            tName = "System";
        }

	    if (banTime > millis && template.getBanPoints() > 0) {

            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `bungeesystem_bansystem_ban` (`id`, `uuid`, `template`, `reason`, `end`, `timestamp`, `team_member`) VALUES (NULL, '"+gebannt.toString()+"', '"+templateName+"', '"+grund+"', '"+banTime+"', '"+millis+"', '"+tName+"') " +
                    "ON DUPLICATE KEY UPDATE `template`='"+templateName+"', `reason`='"+grund+"', `end`='"+banTime+"', `timestamp`='"+millis+"', `team_member`='"+tName+"';");
            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `bungeesystem_bansystem_banhistory` (`id`, `uuid`, `template`, `reason`, `end`, `timestamp`, `team_member`) VALUES (NULL, '" + gebannt.toString() + "', '" + templateName + "', '" + grund + "', " + banTime + ", " + millis + ", '" + tName + "')");
            BungeeCoreSystem.getInstance().getMySQL(1).update("UPDATE userinfo SET status='banned' WHERE uuid='" + gebannt.toString() + "'");

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

            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `bungeesystem_bansystem_mute` (`id`, `uuid`, `template`, `reason`, `end`, `timestamp`, `team_member`) VALUES (NULL, '"+gebannt.toString()+"', '"+templateName+"', '"+grund+"', '"+muteTime+"', '"+millis+"', '"+tName+"') " +
                    "ON DUPLICATE KEY UPDATE `template`='"+templateName+"', `reason`='"+grund+"', `end`='"+muteTime+"', `timestamp`='"+millis+"', `team_member`='"+tName+"';");
            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `bungeesystem_bansystem_mutehistory` (`id`, `uuid`, `template`, `reason`, `end`, `timestamp`, `team_member`) VALUES (NULL, '" + gebannt.toString() + "', '" + templateName + "', '" + grund + "', " + muteTime + ", " + millis + ", '" + tName + "')");

            if (p != null) {
                Messager.sendSimple(p, "\n§8§m----------------§r§8 [§7§l!§8] §fSystem §8§m----------------"
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
	
	public static void unban(UUID uuid){
		BungeeCoreSystem.getInstance().getMySQL(1).update("DELETE FROM bungeesystem_bansystem_ban WHERE uuid ='" + uuid.toString() + "'");
	}

	public static void unmute(UUID uuid) {
		BungeeCoreSystem.getInstance().getMySQL(1).update("DELETE FROM bungeesystem_bansystem_mute WHERE uuid ='" + uuid.toString() + "'");
	}
	
    public static boolean isBanned(UUID uuid){
        long millis = System.currentTimeMillis() / 1000;
        BungeeCoreSystem.getInstance().getMySQL(1).update("DELETE FROM `bungeesystem_bansystem_ban` WHERE end<="+millis);
		return (boolean) BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT `template` FROM `bungeesystem_bansystem_ban` WHERE `uuid`='" + uuid.toString() + "'", rs -> {
            try {
                return rs.next();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return false;
        });
	}

	public static boolean isMuted(UUID uuid){
        long millis = System.currentTimeMillis() / 1000;
        BungeeCoreSystem.getInstance().getMySQL(1).update("DELETE FROM `bungeesystem_bansystem_mute` WHERE end<="+millis);
		return (boolean) BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT `template` FROM `bungeesystem_bansystem_mute` WHERE `uuid` ='" + uuid.toString() + "'", rs -> {
            try {
                return rs.next();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return false;
        });
	}

	private static boolean hasPoints(UUID uuid) {
        return (boolean) BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT `banpoints` FROM `bungeesystem_bansystem_points` WHERE `uuid`='" + uuid.toString() + "'", rs -> {
            try{
                return rs.next();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return false;
        });
    }

	private static Map<String, Integer> getPoints(UUID uuid) {
        return (Map<String, Integer>) BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT `banpoints`, `mutepoints` FROM `bungeesystem_bansystem_points` WHERE `uuid`='" + uuid.toString() + "'", rs -> {
            Map<String, Integer> result = new HashMap<>();
            try{
                if (rs.next()) {
                    result.put("ban", rs.getInt("banpoints"));
                    result.put("mute", rs.getInt("mutepoints"));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return result;
        });
    }

    private static void addPoints(UUID uuid, int banpoints, int mutepoints) {
	    if (hasPoints(uuid)) {
            BungeeCoreSystem.getInstance().getMySQL(1).update("UPDATE `bungeesystem_bansystem_points` SET banpoints=banpoints+" + banpoints + ", mutepoints=mutepoints+" + mutepoints + " WHERE `uuid`='" + uuid.toString() + "'");
        } else {
            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO `bungeesystem_bansystem_points` (`id`, `uuid`, `banpoints`, `mutepoints`) VALUES (NULL, '" + uuid.toString() + "', " + banpoints + ", " + mutepoints + ")");
        }
    }

    private static long getBanTimestampByPoints(int points) {
	    long result;

	    if (points >= 20) {
	        result = 60*60*24*365*10;
	    } else if (points >= 15) {
            result = 60*60*24*60;
	    } else if (points >= 10) {
            result = 60*60*24*30;
        } else if (points >= 5) {
            result = 60*60*24*7;
        } else if (points >= 2) {
            result = 60*60*24;
        } else if (points >= 1) {
            result = 60*60*12;
        } else {
	        result = 0;
        }

        return result;
    }

    private static long getMuteTimestampByPoints(int points) {
        long result;

        if (points >= 20) {
            result = 60*60*24*365*10;
        } else if (points >= 15) {
            result = 60*60*24*60;
        } else if (points >= 10) {
            result = 60*60*24*30;
        } else if (points >= 5) {
            result = 60*60*24*14;
        } else if (points >= 2) {
            result = 60*60*24*2;
        } else if (points >= 1) {
            result = 60*60*24;
        } else {
            result = 0;
        }

        return result;
    }
    
    public static String getEndeString(long end) {
        long millis = System.currentTimeMillis() / 1000;

        if(end > 60*60*24*365+millis) {
            return "§eimmer";
        }else {
             long seconds = end - millis, minutes = 0, hours = 0, days = 0;

             while (seconds > 60) {
                 seconds -= 60;
                 minutes++;
             }

             while (minutes > 60) {
                 minutes -=60;
                 hours++;
             }

             while (hours > 24) {
                 hours -= 24;
                 days++;
             }

             return  "§e" + days + " §7Tag(e), §e" + hours + " §7Stunde(n) und §e" + minutes + " §7Minute(n)";
        }
    }
}

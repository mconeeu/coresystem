/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.api;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.event.StatsChangeEvent;
import eu.mcone.coresystem.lib.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class StatsAPI {

    private String spielmodus, name;
    private MySQL mysql;

	public StatsAPI(String spielmodus, String name, MySQL mysql) {
        this.spielmodus = spielmodus;
        this.name = name;
		this.mysql = mysql;

		createTable();
    }

    public int getKills(UUID uuid) {
        return (int) this.mysql.select("SELECT `kill` FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
            int i = 0;
            try {
                if (rs.next()) {
                    i = rs.getInt("kill");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return i;
        });
    }
	  
    public int getWins(UUID uuid) {
	    return (int) this.mysql.select("SELECT `win` FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
            int i = 0;
            try {
                if (rs.next()) {
                    i = rs.getInt("win");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return i;
        });
    }
	  
    public int getLoses(UUID uuid) {
        return (int) this.mysql.select("SELECT `lose` FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
            int i = 0;
            try {
                if (rs.next()) {
                    i = rs.getInt("lose");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return i;
        });
    }
	  
    public int getDeaths(UUID uuid) {
        return (int) this.mysql.select("SELECT `death` FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
            int i = 0;
            try {
			    if (rs.next()) {
                    i = rs.getInt("death");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return i;
        });
    }
	  
	public void setKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`='" + kill + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void setDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0) ON DUPLICATE KEY UPDATE `death`='" + death + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void setWins(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0) ON DUPLICATE KEY UPDATE `win`='" + win + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void setLose(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, 0, 0, '" + lose + "') ON DUPLICATE KEY UPDATE `lose`='" + lose + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void addKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', '" + kill + "', 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`+" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void addDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`+" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void addWin(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0) ON DUPLICATE KEY UPDATE `win`=`win`+" + win);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void addLose(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ") ON DUPLICATE KEY UPDATE `lose`=`lose`+" + lose);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public void removeKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`-" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	public void removeDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`-" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}
	  
	public int getUserRanking(final UUID uuid){
		return (int) this.mysql.select("SELECT uuid FROM " + this.spielmodus + " ORDER BY `kill` DESC", rs -> {
            boolean done = false;
            int n = 0;

		    try {
                while ((rs.next()) && (!done))
                {
                    n++;
                    if (rs.getString("uuid").equalsIgnoreCase(uuid.toString())) {
                        done = true;
                    }
                }
                return n;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
	}

	public int[] getData(final Player p) {
		return (int[]) CoreSystem.mysql2.select("SELECT * FROM " + this.spielmodus + " WHERE uuid='" + p.getUniqueId() + "'", rs -> {
			try {
				if (rs.next()) {
					return new int[]{getUserRanking(p.getUniqueId()), rs.getInt("kill"), rs.getInt("death")};
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return new int[]{0,0,0};
		});
	}

	private void createTable() {
		mysql.update("CREATE TABLE IF NOT EXISTS " + spielmodus + " (`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, `uuid` VARCHAR(100) NOT NULL UNIQUE KEY, `kill` int(10) NOT NULL, `death` int(10)  NOT NULL, `win` int(10) NOT NULL, `lose` int(10) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
	}

	public String getName() {
		return name;
	}
}

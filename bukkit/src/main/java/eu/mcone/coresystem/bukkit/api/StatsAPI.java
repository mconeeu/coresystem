/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.api;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.event.StatsChangeEvent;
import eu.mcone.coresystem.lib.mysql.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class StatsAPI {

    @Getter
    private String spielmodus, name;
    private MySQL mysql;

	/**
	 * Create a new class object with the values, Spielmodus, name, mysql
	 * @param spielmodus >> Gammemode
	 * @param name >>
	 * @param mysql >> Mysql connection
	 */
	public StatsAPI(String spielmodus, String name, MySQL mysql) {
        this.spielmodus = spielmodus;
        this.name = name;
		this.mysql = mysql;

		createTable();
    }


	/**
	 * Returns the player's current kill, kill's
	 * @param uuid >> Player UniqueID
	 */
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

	/**
	 * Returns the player's current win, win's
	 * @param uuid >> Player UniqueID
	 */
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

	/**
	 * Returns the player's current lose, lose's
	 * @param uuid >> Player UniqueID
	 */
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

	/**
	 * Returns the player's current death, death's
	 * @param uuid >> Player UniqueID
	 */
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

	/**
	 * Returns the gamemodes main goal (is set)
	 * @param uuid >> Player UniqueID
	 */
	public int getGoals(UUID uuid) {
		return (int) this.mysql.select("SELECT `goal` FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
			int i = 0;
			try {
				if (rs.next()) {
					i = rs.getInt("goal");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return i;
		});
	}



	/**
	 * Set the specified integer to the table column kill
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void setKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`='" + kill + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column death
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void setDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`='" + death + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column win
	 * @param uuid >> Player UniqueID
	 * @param win >> specified Integer
	 */
	public void setWins(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0, 0) ON DUPLICATE KEY UPDATE `win`='" + win + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column lose
	 * @param uuid >> Player UniqueID
	 * @param lose >> specified Integer
	 */
	public void setLose(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ", 0) ON DUPLICATE KEY UPDATE `lose`='" + lose + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

    /**
     * Set the specified integer to the table column goal
     * @param uuid >> Player UniqueID
     * @param goal >> specified Integer
     */
    public void setGoal(UUID uuid, int goal) {
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, 0, " + goal + ") ON DUPLICATE KEY UPDATE `goal`='" + goal + "'");
            Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
        });
    }



	/**
	 * Adds the specified integer to the table column kill
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void addKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', '" + kill + "', 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`+" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column death
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void addDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`+" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column win
	 * @param uuid >> Player UniqueID
	 * @param win >> specified Integer
	 */
	public void addWin(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0, 0) ON DUPLICATE KEY UPDATE `win`=`win`+" + win);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column lose
	 * @param uuid >> Player UniqueID
	 * @param lose >> specified Integer
	 */
	public void addLose(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ", 0) ON DUPLICATE KEY UPDATE `lose`=`lose`+" + lose);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

    /**
     * Adds the specified integer to the table column goal
     * @param uuid >> Player UniqueID
     * @param goal >> specified Integer
     */
    public void addGoal(UUID uuid, int goal) {
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, 0, " + goal + ") ON DUPLICATE KEY UPDATE `goal`=`goal`+" + goal);
            Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
        });
    }



	/**
	 * Removes the specified integer (kill) from the player statistics
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void removeKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`-" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Removes the specified integer (Death) from the player statistics
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void removeDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
			this.mysql.update("INSERT INTO " + this.spielmodus + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`-" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(CoreSystem.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Returns Ranking place as Integer of the player back
	 * @param uuid >> Player UniqueID
	 */
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

	/**
	 * Returns the player's current death rate (K.D)
	 * @param uuid >> Player UniqueID
	 */
	public double getKD(UUID uuid) {
		this.mysql.select("SELECT * FROM " + this.spielmodus + " WHERE `uuid`='" + uuid + "'", rs -> {
			int kills = 0;
			int deaths = 0;

			try {
				if (rs.next()) {
					kills = rs.getInt("kills");
					deaths = rs.getInt("deaths");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if(kills == 0 && deaths == 0) {
				return 0.00;
			} else if(kills == 0) {
				return 0.00;
			} else if(deaths == 0) {
				return kills + .00;
			} else {
				return kills / deaths;
			}
		});

		return 0.0;
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

	/**
	 * Creates the table with the specified game mode
	 *
	 * Table columns
	 * uuid >> varchar(100)
	 * kill >> int(100)
	 * death >> int(100)
	 * win >> int(100)
	 * lose >> int(100)
	 * goal >> int(100)
	 */
	private void createTable() {
		mysql.update("CREATE TABLE IF NOT EXISTS " + spielmodus + " " +
				"(" +
				"`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
				"`uuid` VARCHAR(100) NOT NULL UNIQUE KEY, " +
				"`kill` int(100) NOT NULL, " +
				"`death` int(100)  NOT NULL, " +
				"`win` int(100) NOT NULL, " +
				"`lose` int(100) NOT NULL," +
				"`goal` int(100)" +
				") " +
				"ENGINE=InnoDB DEFAULT CHARSET=utf8;");
	}

}

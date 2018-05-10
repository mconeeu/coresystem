/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.event.StatsChangeEvent;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.UUID;

public class StatsAPI implements eu.mcone.coresystem.api.bukkit.player.StatsAPI {
	
    @Getter
    private Gamemode gamemode;
    private BukkitCoreSystem instance;
    private MySQL mySQL;

	/**
	 * Create a new class object with the values, Spielmodus, name, eu.mcone.coresystem.api.core.mysql
	 * @param instance >> BukkitCoreInstance
	 * @param gamemode >> Gamemode
	 */
	public StatsAPI(BukkitCoreSystem instance, Gamemode gamemode) {
		this.gamemode = gamemode;
		this.instance = instance;
		mySQL = instance.getMySQL(Database.STATS);

		createTable();
    }


	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current kill, kill's
	 * @param uuid >> Player UniqueID
	 */
    public int getKills(UUID uuid) {
        return (int) mySQL.select("SELECT `kill` FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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
	 * Returns the eu.mcone.coresystem.api.core.player's current win, win's
	 * @param uuid >> Player UniqueID
	 */
    public int getWins(UUID uuid) {
	    return (int) mySQL.select("SELECT `win` FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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
	 * Returns the eu.mcone.coresystem.api.core.player's current lose, lose's
	 * @param uuid >> Player UniqueID
	 */
    public int getLoses(UUID uuid) {
        return (int) mySQL.select("SELECT `lose` FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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
	 * Returns the eu.mcone.coresystem.api.core.player's current death, death's
	 * @param uuid >> Player UniqueID
	 */
    public int getDeaths(UUID uuid) {
        return (int) mySQL.select("SELECT `death` FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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
		return (int) mySQL.select("SELECT `goal` FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`='" + kill + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column death
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void setDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`='" + death + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column win
	 * @param uuid >> Player UniqueID
	 * @param win >> specified Integer
	 */
	public void setWins(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0, 0) ON DUPLICATE KEY UPDATE `win`='" + win + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Set the specified integer to the table column lose
	 * @param uuid >> Player UniqueID
	 * @param lose >> specified Integer
	 */
	public void setLoses(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ", 0) ON DUPLICATE KEY UPDATE `lose`='" + lose + "'");
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

    /**
     * Set the specified integer to the table column goal
     * @param uuid >> Player UniqueID
     * @param goal >> specified Integer
     */
    public void setGoals(UUID uuid, int goal) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, 0, " + goal + ") ON DUPLICATE KEY UPDATE `goal`='" + goal + "'");
            Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
        });
    }



	/**
	 * Adds the specified integer to the table column kill
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void addKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', '" + kill + "', 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`+" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column death
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void addDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`+" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column win
	 * @param uuid >> Player UniqueID
	 * @param win >> specified Integer
	 */
	public void addWins(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0, 0) ON DUPLICATE KEY UPDATE `win`=`win`+" + win);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Adds the specified integer to the table column lose
	 * @param uuid >> Player UniqueID
	 * @param lose >> specified Integer
	 */
	public void addLoses(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ", 0) ON DUPLICATE KEY UPDATE `lose`=`lose`+" + lose);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

    /**
     * Adds the specified integer to the table column goal
     * @param uuid >> Player UniqueID
     * @param goal >> specified Integer
     */
    public void addGoal(UUID uuid, int goal) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, 0, " + goal + ") ON DUPLICATE KEY UPDATE `goal`=`goal`+" + goal);
            Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
        });
    }



	/**
	 * Removes the specified integer (kill) from the eu.mcone.coresystem.api.core.player statistics
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void removeKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', " + kill + ", 0, 0, 0, 0) ON DUPLICATE KEY UPDATE `kill`=`kill`-" + kill);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Removes the specified integer (Death) from the eu.mcone.coresystem.api.core.player statistics
	 * @param uuid >> Player UniqueID
	 * @param death >> specified Integer
	 */
	public void removeDeaths(UUID uuid, int death) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, " + death + ", 0, 0, 0) ON DUPLICATE KEY UPDATE `death`=`death`-" + death);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Removes the specified integer (kill) from the eu.mcone.coresystem.api.core.player statistics
	 * @param uuid >> Player UniqueID
	 * @param win >> specified Integer
	 */
	public void removeWins(UUID uuid, int win) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, " + win + ", 0, 0) ON DUPLICATE KEY UPDATE `win`=`win`-" + win);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Removes the specified integer (Death) from the eu.mcone.coresystem.api.core.player statistics
	 * @param uuid >> Player UniqueID
	 * @param lose >> specified Integer
	 */
	public void removeLoses(UUID uuid, int lose) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, " + lose + ", 0) ON DUPLICATE KEY UPDATE `lose`=`lose`-" + lose);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Removes the specified integer (Death) from the eu.mcone.coresystem.api.core.player statistics
	 * @param uuid >> Player UniqueID
	 * @param goal >> specified Integer
	 */
	public void removeGoals(UUID uuid, int goal) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			mySQL.update("INSERT INTO " + this.gamemode.toString() + " (`uuid`, `kill`, `death`, `win`, `lose`, `goal`) VALUES ('" + uuid + "', 0, 0, 0, 0, " + goal + ") ON DUPLICATE KEY UPDATE `goal`=`goal`-" + goal);
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Returns Ranking place as Integer of the eu.mcone.coresystem.api.core.player back
	 * @param uuid >> Player UniqueID
	 */
	public int getUserRanking(final UUID uuid){
		return (int) mySQL.select("SELECT uuid FROM " + this.gamemode.toString() + " ORDER BY `kill` DESC", rs -> {
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
	 * Returns the eu.mcone.coresystem.api.core.player's current death rate (K.D)
	 * @param uuid >> Player UniqueID
	 */
	public double getKD(UUID uuid) {
		mySQL.select("SELECT * FROM " + this.gamemode.toString() + " WHERE `uuid`='" + uuid + "'", rs -> {
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

	public int[] getData(final UUID uuid) {
		return (int[]) mySQL.select("SELECT * FROM " + this.gamemode.toString() + " WHERE uuid='" + uuid + "'", rs -> {
			try {
				if (rs.next()) {
					return new int[]{getUserRanking(uuid), rs.getInt("kill"), rs.getInt("death")};
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
		mySQL.update("CREATE TABLE IF NOT EXISTS " + gamemode.toString() + " " +
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

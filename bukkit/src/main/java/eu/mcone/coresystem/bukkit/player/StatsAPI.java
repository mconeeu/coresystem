/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.StatsChangeEvent;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

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
		mySQL = instance.getMySQL(MySQLDatabase.STATS);

		createTable();
    }


	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current kill, kill's
	 * @param uuid >> Player UniqueID
	 */
    public int getKills(UUID uuid) {
		Document killDocument = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (killDocument != null) {
			return killDocument.getInteger("kill");
		}
		return 0;
    }

	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current win, win's
	 * @param uuid >> Player UniqueID
	 */
    public int getWins(UUID uuid) {
		Document winDocument = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (winDocument != null) {
			return winDocument.getInteger("win");
		}
		return 0;
    }

	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current lose, lose's
	 * @param uuid >> Player UniqueID
	 */
    public int getLoses(UUID uuid) {
		Document loseDocument = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (loseDocument != null) {
			return loseDocument.getInteger("lose");
		}
		return 0;
    }

	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current death, death's
	 * @param uuid >> Player UniqueID
	 */
    public int getDeaths(UUID uuid) {
		Document deathDocument = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (deathDocument != null) {
			return deathDocument.getInteger("death");
		}
		return 0;
    }

	/**
	 * Returns the gamemodes main goal (is set)
	 * @param uuid >> Player UniqueID
	 */
	public int getGoals(UUID uuid) {
		Document goalDocument = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (goalDocument != null) {
			return goalDocument.getInteger("goal");
		}
		return 0;
	}



	/**
	 * Set the specified integer to the table column kill
	 * @param uuid >> Player UniqueID
	 * @param kill >> specified Integer
	 */
	public void setKills(UUID uuid, int kill) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), set("kill", kill));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), set("death", death));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), set("win", win));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), set("lose", lose));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), set("goal", goal));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("kill", kill));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("death", death));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("lose", lose));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("kill", -kill));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("death", -death));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("win", -win));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("lose", -lose));
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
			CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).updateOne(eq("uuid", uuid.toString()), inc("goal", -goal));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), this));
		});
	}

	/**
	 * Returns Ranking place as Integer of the eu.mcone.coresystem.api.core.player back
	 * @param uuid >> Player UniqueID
	 */
	public int getUserRanking(final UUID uuid){
		boolean done = false;
		int n = 0;
		for (Document document : CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find()) {
			n++;
			if (document.getString("uuid").equalsIgnoreCase(uuid.toString())) {
				done = true;
				return n;
			}
		}
		return 0;
	}

	/**
	 * Returns the eu.mcone.coresystem.api.core.player's current death rate (K.D)
	 * @param uuid >> Player UniqueID
	 */
	public double getKD(UUID uuid) {
		double kills;
		double deaths;

		Document document = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();
		if (document != null) {
			kills = document.getInteger("kill");
			deaths = document.getInteger("death");

			if(kills == 0 && deaths == 0) {
				return 0.00D;
			} else if(kills == 0) {
				return 0.00D;
			} else if(deaths == 0) {
				return kills + .00D;
			} else {
				return kills / deaths;
			}
		}
		return 0.0;
	}

	public int[] getData(final UUID uuid) {
		Document document = CoreSystem.getInstance().getMongoDB().getCollection(this.gamemode.toString()).find(eq("uuid", uuid.toString())).first();

		if (document != null) {
			return new int[]{getUserRanking(uuid), document.getInteger("kill"), document.getInteger("death")};
		}
		return null;
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

/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.event.StatsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class StatsAPI implements eu.mcone.coresystem.api.bukkit.player.Stats {

    @Getter
    private Gamemode gamemode;

    @Getter
    private int kill = 0, death = 0, win = 0, lose = 0, goal = 0;
    
    private BukkitCoreSystem instance;
    private UUID uuid;
    private MongoCollection<Document> collection;

	/**
	 * Create a new class object with the values, Spielmodus, name, eu.mcone.coresystem.api.core.mysql
	 * @param instance >> BukkitCoreInstance
	 * @param gamemode >> Gamemode
	 */
	StatsAPI(BukkitCoreSystem instance, CorePlayer player, Gamemode gamemode) {
		this.gamemode = gamemode;
		this.uuid = player.getUuid();
		this.instance = instance;
		this.collection = BukkitCoreSystem.getSystem().getMongoDB(Database.STATS).getCollection(gamemode.toString());

		Document entry;
		if ((entry = collection.find(eq("uuid", player.getUuid().toString())).first()) != null) {
			this.kill = entry.getInteger("kill");
			this.death = entry.getInteger("death");
			this.win = entry.getInteger("win");
			this.lose = entry.getInteger("lose");
			this.goal = entry.getInteger("goal");
		} else {
			collection.insertOne(
					new Document("uuid", player.getUuid().toString()).append("kill", 0).append("death", 0).append("win", 0).append("lose", 0).append("goal", 0)
			);
		}
    }


	public void setKills(int kill) {
		this.kill = kill;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), set("kill", kill));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void setDeaths(int death) {
		this.death = death;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), set("death", death));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void setWins(int win) {
		this.win = win;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), set("win", win));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void setLoses(int lose) {
		this.lose = lose;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), set("lose", lose));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

    public void setGoals(int goal) {
		this.goal = goal;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), set("goal", goal));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
    }


	public void addKills(int kill) {
		this.kill += kill;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("kill", kill));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void addDeaths(int death) {
		this.death += death;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("death", death));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void addWins(int win) {
		this.win += win;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("win", win));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void addLoses(int lose) {
		this.lose += lose;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("lose", lose));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

    public void addGoal(int goal) {
		this.goal += goal;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("goal", goal));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
    }


	public void removeKills(int kill) {
		this.kill -= kill;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("kill", -kill));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void removeDeaths(int death) {
		this.death -= death;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("death", -death));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void removeWins(int win) {
		this.win -= win;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("win", -win));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void removeLoses(int lose) {
		this.lose -= lose;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("lose", -lose));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}

	public void removeGoals(int goal) {
		this.goal -= goal;

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			collection.updateOne(eq("uuid", uuid.toString()), inc("goal", -goal));
			Bukkit.getPluginManager().callEvent(new StatsChangeEvent(instance.getCorePlayer(uuid), gamemode));
		});
	}


	public int getUserRanking(){
		int n = 0;

		for (Document user : collection.find().sort(orderBy(descending("kill")))) {
			n++;
			if (user.getString("uuid").equals(uuid.toString())) return n;
		}

		return n;
	}

	public double getKD() {
		double kill = this.kill;
		double death = this.death;

		if(kill == 0 && death == 0) {
			return 0.00D;
		} else if (kill == 0) {
			return 0.00D;
		} else if (death == 0) {
			return kill + .00D;
		} else {
			return kill / death;
		}
	}

	public int[] getData() {
		return new int[]{getUserRanking(), kill, death};
	}

}

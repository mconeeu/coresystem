package eu.mcone.coresystem.api.bukkit.stats;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.*;

public class CoreStatsManager {

    private final MongoDatabase statsDatabase;

    public CoreStatsManager() {
        statsDatabase = CoreSystem.getInstance().getStatsDB();
    }

    public void save(CoreStats stats) {
        if (stats.getUuid() != null) {
            statsDatabase.getCollection(stats.getGamemode().toString(), CoreStats.class).replaceOne(
                    eq("uuid", stats.getUuid()),
                    stats,
                    new ReplaceOptions().upsert(true)
            );
        } else {
            throw new RuntimeException("UUID Field in CoreStats is null! The Player constructor must be used!");
        }
    }

    public <S> MongoCollection<S> getStatsCollection(Gamemode gamemode, Class<S> clazz) {
        return statsDatabase.getCollection(gamemode.toString(), clazz);
    }

    public int getUserRanking(Gamemode gamemode, UUID uuid, StatsOrder order) {
        int n = 0;

        //TODO: Check if this works!
        if (order == StatsOrder.KD) {
            for (Document user : statsDatabase.getCollection(gamemode.toString()).find().sort(orderBy(ascending("kills"), descending("deaths")))) {
                n++;
                if (user.getString("uuid").equals(uuid.toString())) return n;
            }
        } else {
            for (Document user : statsDatabase.getCollection(gamemode.toString()).find().sort(orderBy(descending(order.getMongoField())))) {
                n++;
                if (user.getString("uuid").equals(uuid.toString())) return n;
            }
        }

        return n;
    }

    public <S> S getStats(Gamemode gamemode, UUID uuid, Class<S> clazz) {
        S stats = statsDatabase.getCollection(gamemode.toString(), clazz).find(eq("uuid", uuid)).first();

        if (stats != null) {
            return stats;
        } else {
            try {
                return clazz.getDeclaredConstructor(Gamemode.class, UUID.class).newInstance(gamemode, uuid);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

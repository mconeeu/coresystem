/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.overwatch;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.overwatch.util.Statistic;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;

import java.util.Calendar;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

@Getter
public class GlobalOverwatch implements eu.mcone.coresystem.api.core.overwatch.GlobalOverwatch {

    private Statistic statistic;
    private final MongoCollection<Statistic> statisticsCollection;

    public GlobalOverwatch(GlobalCoreSystem instance) {
        statisticsCollection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("overwatch_statistics", Statistic.class);
        createStatistic();
    }

    private void createStatistic() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        String id = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + "" + cal.get(Calendar.DAY_OF_MONTH);

        Statistic statistic = statisticsCollection.find(eq("statisticID", id)).first();

        if (statistic != null) {
            this.statistic = statistic;
        } else {
            this.statistic = new Statistic(System.currentTimeMillis() / 1000, id);
            statisticsCollection.insertOne(this.statistic);
        }
    }

    public void addBan() {
        statistic.addBan();
        statisticsCollection.updateOne(eq("id", statistic.getStatisticID()), inc("bans", 1));
    }

    public void addReport() {
        statistic.addReport();
        statisticsCollection.updateOne(eq("id", statistic.getStatisticID()), inc("reports", 1));
    }

    public void addMute() {
        statistic.addMute();
        statisticsCollection.updateOne(eq("id", statistic.getStatisticID()), inc("mutes", 1));
    }

    public void addBotAttacks(int botAttacks) {
        statistic.addBotAttacks(botAttacks);
        statisticsCollection.updateOne(eq("id", statistic.getStatisticID()), inc("botAttacks", botAttacks));
    }
}

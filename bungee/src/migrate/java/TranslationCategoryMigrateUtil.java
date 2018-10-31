/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

import com.mongodb.client.MongoCollection;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.database.MongoConnection;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class TranslationCategoryMigrateUtil {

    public static void main(String[] args) {
        MongoCollection<Document> collection = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017)
                .connect()
                .getDatabase(Database.SYSTEM)
                .getCollection("translations");

        System.out.println(
                collection.updateMany(
                        eq("category", "Minewar"),
                        set("category", "minewar")
                )
        );
    }

}

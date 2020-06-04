/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

import com.mongodb.client.MongoCollection;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.database.MongoConnection;
import org.bson.Document;

import java.util.ArrayList;

public class FriendsMigateTest {

    public static void main(String[] args) {
        MongoCollection<Document> collection = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017)
                .connect()
                .getDatabase(Database.SYSTEM)
                .getCollection("bungeesystem_friends");

        System.out.println(collection.find().first().get("friends", new ArrayList<String>()).get(0));
    }

}

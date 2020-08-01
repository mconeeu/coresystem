/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.database.MongoConnection;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class NickMigrateUtil {

    public static void main(String[] args) {
        MongoConnection con = new MongoConnection(
                "db.mcone.eu",
                "admin",
                "Ze7OCxrVI30wmJU38TX9UmpoL8RnLPogmV3sIljcD2HQkth86bzr6JRiaDxabdt8",
                "admin",
                27017
        ).codecRegistry(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                        new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                        PojoCodecProvider.builder().automatic(true).build()
                )
        ).connect();

        MongoCollection<Document> nicks = con.getDatabase(Database.SYSTEM).getCollection("nicks");

        for (Document document : nicks.find()) {
            nicks.updateOne(eq(document.getObjectId("_id")), combine(
                    set("online_time", (long) document.getInteger("onlinetime")),
                    set("nick_uuid", document.getString("nickUuid")),
                    unset("onlinetime"),
                    unset("nickUuid")
            ));
        }
    }

}

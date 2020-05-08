/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.database.MongoConnection;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Random;

import static com.mongodb.client.model.Filters.eq;

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

        MongoCollection<Document> old = con.getDatabase(Database.SYSTEM).getCollection("bungeesystem_nicks_old");
        MongoCollection<Document> skins = con.getDatabase(Database.SYSTEM).getCollection("bungeesystem_textures");
        MongoCollection<Document> nicks = con.getDatabase(Database.SYSTEM).getCollection("nicks");

        Random random = new Random();
        for (Document doc : old.find()) {
            System.out.println("migrating nick "+doc.getString("name"));
            Document skin = skins.find(
                    eq("name", doc.getString("texture"))
            ).first();

            if (skin == null) {
                System.out.println("could not find texture "+doc.getString("texture"));
            } else {
                nicks.insertOne(
                        new Document("name", doc.getString("name"))
                                .append("group", (random.nextInt(1) == 0 ? Group.SPIELER : Group.PREMIUM).toString())
                                .append("texture_value", skin.getString("texture_value"))
                                .append("texture_signature", skin.getString("texture_signature"))
                                .append("coins", random.nextInt(100) + 50)
                                .append("onlineTime", random.nextInt(1000) + 10)
                );
            }
        }
    }

}

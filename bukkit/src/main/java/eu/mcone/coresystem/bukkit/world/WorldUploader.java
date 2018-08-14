/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.util.Zip;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import eu.mcone.networkmanager.core.api.database.Database;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

class WorldUploader {

    private CoreWorld world;
    private MySQLDatabase database;
    private String table = "mc1cloud.cloudwrapper_worlds";

    WorldUploader(CoreWorld world) {
        if (world.bukkit() != null) this.world = world;
    }

    WorldUploader(CoreWorld world, MySQLDatabase database, String table) {
        this.world = world;
        this.database = database;
        this.table = table;
    }

    boolean upload() {
        world.save();

        World w = world.bukkit();
        w.setAutoSave(false);
        w.save();

        File worldFile = w.getWorldFolder();
        File zipFile = new File(world.getName() + ".zip");

        if (zipFile.exists()) zipFile.delete();
        new Zip(worldFile, zipFile);

        try {
            FileInputStream fis = new FileInputStream(zipFile);
            MongoCollection<Document> cloudCollection = BukkitCoreSystem.getSystem().getMongoDB(Database.CLOUD).getCollection("cloudwrapper_worlds");
            Document document = cloudCollection.find(eq("name", world.getName())).first();

            int build = 0;
            if (document != null) {
                build = document.getInteger("build");

                BukkitCoreSystem.getSystem().getMongoDB(Database.CLOUD).getCollection("cloudwrapper_worlds")
                        .updateOne(eq("name", world.getName()), combine(
                                set("build", ++build),
                                set("name", world.getName()),
                                set("bytes", IOUtils.toByteArray(fis))));
            } else {
                BukkitCoreSystem.getSystem().getMongoDB(Database.CLOUD).getCollection("cloudwrapper_worlds")
                        .insertOne(new Document("build", ++build)
                                .append("name", world.getName())
                                .append("bytes", IOUtils.toByteArray(fis)));
            }

            fis.close();

            zipFile.delete();
            w.setAutoSave(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
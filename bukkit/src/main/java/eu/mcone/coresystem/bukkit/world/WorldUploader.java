/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.util.Zip;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

class WorldUploader {

    private final CoreWorld world;
    private final MongoCollection<Document> collection;

    WorldUploader(CoreWorld world) {
        this.world = world;
        this.collection = BukkitCoreSystem.getSystem().getMongoDB(Database.CLOUD).getCollection("cloudwrapper_worlds");
    }

    WorldUploader(CoreWorld world, MongoCollection<Document> collection) {
        this.world = world;
        this.collection = collection;
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
            Document document = collection.find(eq("name", world.getName())).projection(include("build")).first();

            int build = 0;
            if (document != null) {
                build = document.getInteger("build");

                collection.updateOne(eq("name", world.getName()), combine(
                                set("build", ++build),
                                set("name", world.getName()),
                                set("bytes", IOUtils.toByteArray(fis))));
            } else {
                collection.insertOne(new Document("build", ++build)
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
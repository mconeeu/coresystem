/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.util.Zip;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import org.apache.commons.io.IOUtils;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

class WorldUploader {

    private CoreWorld world;
    private Database database;
    private String table = "mc1cloud.cloudwrapper_worlds";

    WorldUploader(CoreWorld world) {
        if (world.bukkit() != null) this.world = world;
    }

    WorldUploader(CoreWorld world, Database database, String table) {
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
            Connection con = this.database != null ? BukkitCoreSystem.getSystem().getMySQL(this.database).getConnection() : DriverManager.getConnection("jdbc:mariadb://db.mcone.eu:3306/mc1cloud", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG");
            PreparedStatement ps = con.prepareStatement("SELECT build FROM "+this.table+" WHERE `name`='" + world.getName() + "'");
            ResultSet rs = ps.executeQuery();

            int build = 0;
            PreparedStatement send;
            if (rs.next()) {
                build = rs.getInt("build");

                send = con.prepareStatement("UPDATE "+this.table+" SET build=?, `name`=?, bytes=? WHERE `name`='" + world.getName() + "'");
            } else {
                send = con.prepareStatement("INSERT INTO "+this.table+" (build, `name`, bytes) VALUES (?, ?, ?)");
            }

            FileInputStream fis = new FileInputStream(zipFile);

            send.setInt(1, ++build);
            send.setString(2, world.getName());
            send.setBytes(3, IOUtils.toByteArray(fis));
            send.executeUpdate();

            fis.close();
            send.close();
            ps.close();
            con.close();

            zipFile.delete();
            w.setAutoSave(true);
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
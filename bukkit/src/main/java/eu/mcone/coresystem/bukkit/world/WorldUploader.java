/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.Gson;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldProperties;
import eu.mcone.coresystem.api.core.util.Zip;
import org.apache.commons.io.IOUtils;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WorldUploader {

    private CoreWorld world;

    WorldUploader(CoreWorld world) {
        if (world.bukkit() != null) this.world = world;
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
            Connection con = DriverManager.getConnection("jdbc:mariadb://mysql.mcone.eu:3306/mc1cloud", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG");
            PreparedStatement ps = con.prepareStatement("SELECT build FROM mc1cloud.cloudwrapper_worlds WHERE `name`='" + world.getName() + "'");
            ResultSet rs = ps.executeQuery();

            int build = 0;
            PreparedStatement send;
            if (rs.next()) {
                build = rs.getInt("build");

                send = con.prepareStatement("UPDATE mc1cloud.cloudwrapper_worlds SET build=?, `name`=?, `template_name`=?, bytes=? WHERE `name`='" + world.getName() + "'");
            } else {
                send = con.prepareStatement("INSERT INTO mc1cloud.cloudwrapper_worlds (build, `name`, template_name, bytes) VALUES (?, ?, ?, ?)");
            }

            FileInputStream fis = new FileInputStream(zipFile);

            send.setInt(1, ++build);
            send.setString(2, world.getName());
            send.setString(3, world.getTemplateName());
            send.setBytes(4, IOUtils.toByteArray(fis));
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

    private static String insertAsJson(WorldProperties properties) {
        List<String> result = new ArrayList<>(Arrays.asList(
                String.valueOf(properties.isAutoSave()),
                String.valueOf(properties.isPvp()),
                String.valueOf(properties.isAllowAnimals()),
                String.valueOf(properties.isAllowMonsters()),
                String.valueOf(properties.isKeepSpawnInMemory())
        ));

        return new Gson().toJson(result);
    }
}
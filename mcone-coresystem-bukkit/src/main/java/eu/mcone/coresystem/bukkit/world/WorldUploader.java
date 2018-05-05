/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.Gson;
import eu.mcone.coresystem.api.core.util.Zip;
import org.apache.commons.io.IOUtils;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class WorldUploader {

    private World world;

    public WorldUploader(World world) throws FileNotFoundException {
        if (world != null) {
            this.world = world;
        } else {
            throw new FileNotFoundException("World does not exist!");
        }
    }

    public void upload() throws SQLException, IOException {
        world.setAutoSave(false);
        world.save();

        File worldFile = world.getWorldFolder();
        File zipFile = new File(world.getName() + ".zip");

        if (zipFile.exists()) zipFile.delete();
        new Zip(worldFile, zipFile);

        Connection con = DriverManager.getConnection("jdbc:mysql://mysql.mcone.eu:3306/mc1cloud", "mc1cloud", "5CjLP5dHYXQPX85zPizx5hayz0AYNOuNmzcegO0Id0AXnp3w1OJ3fkEQxbGJZAuJ");
        PreparedStatement ps = con.prepareStatement("SELECT `build` FROM `cloudwrapper_worlds` WHERE `name`='" + world.getName() + "'");
        ResultSet rs = ps.executeQuery();

        int build = 0;
        PreparedStatement send;
        if (rs.next()) {
            build = rs.getInt("build");
            send = con.prepareStatement("UPDATE `cloudwrapper_worlds` SET `build`=?, `name`=?, `world_type`=?, `environment`=?, `difficulty`=?, `spawn_location`=?, `generator`=?, `properties`=?, `bytes`=? WHERE `name`='" + world.getName() + "'");
        } else {
            send = con.prepareStatement("INSERT INTO `cloudwrapper_worlds` (`build`, `name`, `world_type`, `environment`, `difficulty`, `spawn_location`, `generator`, `properties`, `bytes`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }

        String generator = world.getGenerator() != null ? world.getGenerator().toString() : null;
        FileInputStream fis = new FileInputStream(zipFile);

        send.setInt(1, ++build);
        send.setString(2, world.getName());
        send.setString(3, world.getWorldType().toString());
        send.setString(4, world.getEnvironment().toString());
        send.setString(5, world.getDifficulty().toString());
        send.setString(6, "[" + (int) world.getSpawnLocation().getX() + "," + (int) world.getSpawnLocation().getY() + "," + (int) world.getSpawnLocation().getZ() + "]");
        send.setString(7, generator);
        send.setString(8, new Gson().toJson(new WorldProperties(world.getPVP(), world.canGenerateStructures(), world.getAllowAnimals(), world.getAllowMonsters(), world.getKeepSpawnInMemory()), WorldProperties.class));
        send.setBytes(9, IOUtils.toByteArray(fis));
        send.executeUpdate();

        fis.close();
        send.close();
        ps.close();
        con.close();

        zipFile.delete();
        world.setAutoSave(true);
    }


}

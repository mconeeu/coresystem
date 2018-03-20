/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.lib.util.UnZip;
import eu.mcone.coresystem.lib.util.Zip;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WorldLoader {

    private String pluginName;
    private static LinkedHashSet<World> loadedWorlds = new LinkedHashSet<>();

    public WorldLoader(String pluginName) {
        this.pluginName = pluginName;
    }

    public void loadWorlds() {
        CoreSystem.mysql1.select("SELECT name FROM bukkitsystem_worlds WHERE server='"+pluginName+"'", rs -> {
            try {
                while (rs.next()) {
                    String name = rs.getString("name");

                    File world = new File(System.getProperty("user.dir") + File.separator + name);
                    File zipFile = new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "worlds" + File.separator + name + ".zip");

                    if (Bukkit.getWorld(name) != null) {
                        FileUtils.deleteDirectory(world);
                    }

                    Bukkit.getConsoleSender().sendMessage("Downloading world "+name+" from database...");
                    CoreSystem.mysql1.select("SELECT bytes FROM bukkitsystem_worlds WHERE name='"+name+"' AND server='"+pluginName+"'", rs1 -> {
                        try {
                            rs1.next();

                            FileOutputStream fos = new FileOutputStream(zipFile);
                            fos.write(rs1.getBytes("bytes"));

                            Bukkit.getConsoleSender().sendMessage("Unzipping world "+name+"...");
                            if (world.exists()) FileUtils.deleteDirectory(world);
                            new UnZip(zipFile.getPath(), world.getPath());

                            World w = Bukkit.createWorld(new WorldCreator(name));
                            loadedWorlds.add(w);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void storeWorld(String name) {
        World w = Bukkit.getWorld(name);
        if (w != null) {
            w.save();

            File world = new File(System.getProperty("user.dir") + File.separator + name);
            File zipFile = new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "worlds" + File.separator + name + ".zip");

            if (zipFile.exists()) zipFile.delete();
            new Zip(world, zipFile);

            try {
                CoreSystem.mysql1.update("DELETE FROM bukkitsystem_worlds WHERE name='" + name + "' AND server='" + pluginName + "'");

                FileInputStream fis = new FileInputStream(zipFile);
                byte[] bytes = IOUtils.toByteArray(fis);

                Connection con = CoreSystem.mysql1.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO bukkitsystem_worlds (name, bytes, server) VALUES (?, ?, ?);");
                ps.setString(1, name);
                ps.setBytes(2, bytes);
                ps.setString(3, pluginName);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("World "+name+" does not exist!");
        }
    }

    public static Set<World> getLoadedWorlds() {
        return loadedWorlds;
    }

}

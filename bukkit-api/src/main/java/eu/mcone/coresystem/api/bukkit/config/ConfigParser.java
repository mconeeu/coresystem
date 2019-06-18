/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

@Log
public class ConfigParser {

    public ConfigParser() {
    }

    public <T> T loadAndParseConfig(final String path, final String fileName, final Class<T> classOfT) {
        return this.parse(new File(path), new File(path, fileName), classOfT);
    }

    public <T> T loadAndParseConfig(final String fileName, final Class<T> classOfT) {
        return this.parse(new File("./plugins/"), new File("./plugins/", fileName), classOfT);
    }

    public <T> T parseToClass(final File file, final Class<T> classOfT) {
        return this.parse(file, file, classOfT);
    }

    private <T> T parse(final File dir, final File file, final Class<T> classOfT) {
        log.info("Parse file `" + file.getName() + "` to class...");
        try {
            if (dir.exists()) {
                if (file.exists()) {
                    return CoreSystem.getInstance().getGson().fromJson(new FileReader(file), classOfT);
                } else {
                    if (file.createNewFile()) {
                        write(classOfT.newInstance(), dir, file);
                        return classOfT.newInstance();
                    } else {
                        log.log(Level.SEVERE, "Cannot create file...");
                    }
                }
            } else {
                if (dir.mkdir()) {
                    write(classOfT.newInstance(), dir, file);
                    return classOfT.newInstance();
                } else {
                    log.log(Level.SEVERE, "Cannot create dir...");
                }
            }
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean parseToFile(final Object obj, final String fileName) {
        log.info("Parse class to file `" + fileName + "`...");

        File file = new File("./plugins/", fileName);
        File dir = new File("./plugins/");
        return this.write(obj, dir, file);
    }

    public boolean parseToFile(final Object obj, final String path, final String fileName) {
        log.info("Parse class to file `" + fileName + "`...");
        File dir = new File(path, fileName);
        File file = new File(fileName);
        return this.write(obj, dir, file);
    }

    private boolean write(final Object obj, final File dir, final File file) {
        try {
            FileWriter fileWriter;


            String jsonString = CoreSystem.getInstance().getGson().toJson(obj);

            if (jsonString != null) {
                if (dir.exists()) {
                    if (file.exists()) {
                        fileWriter = new FileWriter(file);
                        fileWriter.write(jsonString);
                        fileWriter.flush();
                        fileWriter.close();
                    } else {
                        if (file.createNewFile()) {
                            fileWriter = new FileWriter(file);
                            fileWriter.write(jsonString);
                            fileWriter.flush();
                            fileWriter.close();
                        } else {
                            log.log(Level.SEVERE, "Cannot create file...");
                        }
                    }
                } else {
                    if (dir.mkdir()) {
                        log.info("Create dir...");

                        if (file.createNewFile()) {
                            fileWriter = new FileWriter(file);
                            fileWriter.write(jsonString);
                            fileWriter.flush();
                            fileWriter.close();
                            return true;
                        } else {
                            log.log(Level.SEVERE, "Cannot create file...");
                        }
                    } else {
                        log.log(Level.SEVERE, "Cannot create dir...");
                    }
                }
            } else {
                log.log(Level.SEVERE, "Json string is null!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

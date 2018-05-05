/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.mysql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class MySQL_Config {

    private MySQL mysql;
    private String config;
    private int messagelength;
    private boolean tableExists = false;

    private Map<String, String> keys = new HashMap<>();

    public MySQL_Config(MySQL mysql, String config, int messagelength) {
        this.mysql = mysql;
        this.config = config;
        this.messagelength = messagelength;
    }

    public void store() {
        this.mysql.select("SELECT * FROM " + this.config + ";", rs -> {
            try {
                while (rs.next()) {
                    this.keys.put(rs.getString("key"), rs.getString("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.tableExists = false;
    }

    public void createTable() {
        this.mysql.update("CREATE TABLE IF NOT EXISTS `" + this.config + "` (`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, `key` VARCHAR(100) UNIQUE KEY, `value` VARCHAR(" + this.messagelength + ")) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        this.mysql.select("SELECT value FROM " + this.config, rs -> {
            try {
                if (rs.next()) {
                    tableExists = true;
                    System.out.println(this.config + " Config Table existiert bereits. Values werden heruntergeladen...");
                } else {
                    System.out.println(this.config + " Config Table existiert noch nicht. Values werden hochgeladen...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    public void insertMySQLConfig(String key, String value){
        if (!this.tableExists) {
            this.mysql.update("INSERT IGNORE INTO " + this.config + " (`key`, `value`) VALUES ('"+key+"', '"+value+"')");
        }
    }

    public void insertMySQLConfig(String key, int value){
        if (!this.tableExists) {
            this.mysql.update("INSERT IGNORE INTO " + this.config + " (`key`, `value`) VALUES ('" + key + "', " + value + ")");
        }
    }

    public void insertMySQLConfig(String key, boolean value){
        if (!this.tableExists) {
            this.mysql.update("INSERT IGNORE INTO " + this.config + " (`key`, `value`) VALUES ('" + key + "', " + value + ")");
        }
    }


    public void updateMySQLConfig(String key, String value){
        this.mysql.update("INSERT INTO " + this.config + " (`key`, `value`) VALUES ('" + key + "', '" + value + "') ON DUPLICATE KEY UPDATE `value`='" + value +"'");
    }

    public void updateMySQLConfig(String key, boolean value){
        this.mysql.update("INSERT INTO " + this.config + " (`key`, `value`) VALUES ('" + key + "', '" + value + "') ON DUPLICATE KEY UPDATE `value`='" + value +"'");
    }

    public String getConfigValue(String key) {
        return keys.get(key) != null ? keys.get(key).replaceAll("&", "§") : null;
    }

    public int getIntConfigValue(String key) {
        return Integer.parseInt(keys.get(key));
    }

    public Boolean getBooleanConfigValue(String key) {
        String result = keys.get(key);

        switch (result) {
            case "0":
            case "false":
                return false;
            case "1":
            case "true":
                return true;
            default:
                return null;
        }
    }

    public String getLiveConfigValue(String key) {
        return (String) this.mysql.select("SELECT value FROM `" + this.config + "` WHERE `key` = '" + key + "';", rs -> {
            try{
                if (rs.next()) {
                    return rs.getString("value").replace("&", "§");
                }
            }catch(SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public int getLiveIntConfigValue(String key) {
        return (int) this.mysql.select("SELECT * FROM `" + this.config + "` WHERE `key` = '" + key + "';", rs -> {
            try{
                if (rs.next()) {
                    return rs.getInt("value");
                }
            }catch(SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public boolean getLiveBooleanConfigValue(String key) {
        return (boolean) this.mysql.select("SELECT * FROM `" + this.config + "` WHERE `key` = '" + key + "';", rs -> {
            try{
                if (rs.next()) {
                    return rs.getBoolean("value");
                }
            }catch(SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}


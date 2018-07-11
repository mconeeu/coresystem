/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.friend;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.FriendData;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQL;
import net.md_5.bungee.api.ProxyServer;

import java.sql.SQLException;
import java.util.*;

public class FriendSystem implements eu.mcone.coresystem.api.bungee.player.FriendSystem {

    private MySQL mysql;

    public FriendSystem(MySQL mysql) {
        this.mysql = mysql;
    }

    public FriendData getData(UUID uuid) {
        return this.mysql.select("SELECT f.uuid, f.target, f.key, u.uuid, u.name FROM bungeesystem_friends f, userinfo u WHERE f.target = u.uuid AND f.uuid = '"+uuid.toString()+"';", rs -> {
            Map<UUID, String> friends = new HashMap<>();
            Map<UUID, String> requests = new HashMap<>();
            List<UUID> blocks = new ArrayList<>();

            try {
                while (rs.next()) {
                    switch (rs.getString("f.key")){
                        case "friend": friends.put(UUID.fromString(rs.getString("f.target")), rs.getString("u.name")); break;
                        case "request": requests.put(UUID.fromString(rs.getString("f.target")), rs.getString("u.name")); break;
                        case "block": blocks.add(UUID.fromString(rs.getString("f.target"))); break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new eu.mcone.coresystem.bungee.player.FriendData(friends, requests, blocks);
        }, FriendData.class);
    }

    public void addFriend(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getFriends().put(friend, friendName);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','friend' , " + millis + ")");
        });
    }

    public void removeFriend(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getFriends().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='friend'");
        });
    }

    public void addRequest(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getRequests().put(friend, friendName);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','request' , " + millis + ")");
        });
    }

    public void removeRequest(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getRequests().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='request'");
        });
    }

    public void addBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getBlocks().add(friend);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','block' , " + millis + ")");
        });
    }

    public void removeBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getBlocks().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='block'");
        });
    }

    public void addToggled(UUID player) {
        CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
        if (p != null) p.getSettings().setEnableFriendRequests(true);

        long millis = System.currentTimeMillis() / 1000;

        this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES (NULL, '" + player + "', 'toggled', " + millis + ")");
    }

    public void removeToggled(UUID player) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getSettings().setEnableFriendRequests(true);


            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `target`='" + player.toString() + "' AND `key`='toggled'");
        });
    }

}

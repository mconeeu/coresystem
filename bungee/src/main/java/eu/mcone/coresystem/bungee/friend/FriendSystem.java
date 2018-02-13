/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.friend;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.lib.mysql.MySQL;
import net.md_5.bungee.api.ProxyServer;

import java.sql.SQLException;
import java.util.*;

public class FriendSystem {

    private MySQL mysql;

    public FriendSystem(MySQL mysql) {
        this.mysql = mysql;
    }

    public Object[] getData(UUID uuid) {
        return (Object[]) this.mysql.select("SELECT f.uuid, f.target, f.key, u.uuid, u.name FROM bungeesystem_friends f, userinfo u WHERE f.target = u.uuid AND f.uuid = '"+uuid.toString()+"';", rs -> {
            Map<UUID, String> friends = new HashMap<>();
            Map<UUID, String> requests = new HashMap<>();
            List<UUID> blocks = new ArrayList<>();
            boolean requestsToggled = false;

            try {
                while (rs.next()) {
                    switch (rs.getString("f.key")){
                        case "friend": friends.put(UUID.fromString(rs.getString("f.target")), rs.getString("u.name")); break;
                        case "request": requests.put(UUID.fromString(rs.getString("f.target")), rs.getString("u.name")); break;
                        case "block": blocks.add(UUID.fromString(rs.getString("f.target"))); break;
                        case "toggled": requestsToggled = true; break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new Object[]{friends, requests, blocks, requestsToggled};
        });
    }

    public void addFriend(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getFriends().put(friend, friendName);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','friend' , " + millis + ")");
        });
    }

    public void removeFriend(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getFriends().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='friend'");
        });
    }

    public void addRequest(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getRequests().put(friend, friendName);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','request' , " + millis + ")");
        });
    }

    public void removeRequest(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getRequests().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='request'");
        });
    }

    public void addBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getBlocks().add(friend);

            long millis = System.currentTimeMillis() / 1000;

            this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES ('" + player.toString() + "', '" + friend.toString() + "','block' , " + millis + ")");
        });
    }

    public void removeBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.getBlocks().remove(friend);

            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `uuid`='" + player.toString() + "' AND `target`='" + friend.toString() + "' AND `key`='block'");
        });
    }

    public void addToggled(UUID player) {
        CorePlayer p = CoreSystem.getCorePlayer(player);
        if (p != null) p.setRequestsToggled(true);

        long millis = System.currentTimeMillis() / 1000;

        this.mysql.update("INSERT INTO `bungeesystem_friends` (`uuid`, `target`, `key`, `timestamp`) VALUES (NULL, '" + player + "', 'toggled', " + millis + ")");
    }

    public void removeToggled(UUID player) {
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
            CorePlayer p = CoreSystem.getCorePlayer(player);
            if (p != null) p.setRequestsToggled(false);


            this.mysql.update("DELETE FROM `bungeesystem_friends` WHERE `target`='" + player.toString() + "' AND `key`='toggled'");
        });
    }

}

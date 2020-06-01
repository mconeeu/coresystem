/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.friend;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.player.FriendData;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;

public class FriendSystem implements eu.mcone.coresystem.api.bungee.player.FriendSystem {

    private static final MongoDatabase DATABASE = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM);

    public FriendData getData(UUID uuid) {
        Map<UUID, String> names = new HashMap<>();
        Map<UUID, String> friends = new HashMap<>();
        Map<UUID, String> requests = new HashMap<>();
        List<UUID> blocks;
        List<UUID> toggled;

        Document entry = DATABASE.getCollection("bungeesystem_friends").find(eq("uuid", uuid.toString())).first();

        if (entry != null) {
            for (Document player : DATABASE.getCollection("userinfo").find(
                    or(
                            in("uuid", entry.get("friends", new ArrayList<String>())),
                            in("uuid", entry.get("requests", new ArrayList<String>()))
                    )
            )) {
                if (player.getString("name") != null) {
                    names.put(UUID.fromString(player.getString("uuid")), player.getString("name"));
                }
            }

            for (String friend : entry.get("friends", new ArrayList<String>())) {
                UUID uuid1;
                friends.put((uuid1 = UUID.fromString(friend)), names.get(uuid1));
            }
            for (String request : entry.get("requests", new ArrayList<String>())) {
                UUID uuid1;
                requests.put((uuid1 = UUID.fromString(request)), names.get(uuid1));
            }
            blocks = entry.get("block", new ArrayList<>());
            toggled = entry.get("toggled", new ArrayList<>());
        } else {
            blocks = new ArrayList<>();
            toggled = new ArrayList<>();
        }

        return new FriendData(friends, requests, blocks, toggled);
    }

    public void addFriend(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getFriends().put(friend, friendName);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    push("friends", friend.toString()),
                    new UpdateOptions().upsert(true)
            );
        });
    }

    public void removeFriend(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getFriends().remove(friend);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    pull("friends", friend.toString())
            );
        });
    }

    public void addRequest(UUID player, UUID friend, String friendName) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getRequests().put(friend, friendName);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    push("requests", friend.toString()),
                    new UpdateOptions().upsert(true)
            );
        });
    }

    public void removeRequest(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getRequests().remove(friend);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    pull("requests", friend.toString())
            );
        });
    }

    public void addBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getBlocks().add(friend);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    push("blocks", friend.toString()),
                    new UpdateOptions().upsert(true)
            );
        });
    }

    public void removeBlock(UUID player, UUID friend) {
        ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
            CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(player);
            if (p != null) p.getFriendData().getBlocks().remove(friend);

            DATABASE.getCollection("bungeesystem_friends").updateOne(
                    eq("uuid", player.toString()),
                    pull("blocks", friend.toString())
            );
        });
    }

}

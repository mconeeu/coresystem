/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.database.MongoConnection;
import org.bson.Document;

import java.util.*;

public class PermissionMigateTest {

    public static void main(String[] args) {
        String servername = "skypvp";

        Map<Group, Set<String>> groups = new HashMap<>();
        Map<Group, Set<Group>> parents = new HashMap<>();
        HashMap<UUID, Set<String>> permissions = new HashMap<>();

        MongoDatabase database = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017)
                .connect()
                .getDatabase(Database.SYSTEM);

        for (Document entry : database.getCollection("permission_groups").find()) {
            Group g = Group.getGroupById(entry.getInteger("id"));

            Set<String> perms = new HashSet<>();
            for (Map.Entry<String, Object> e : entry.get("permissions", new Document()).entrySet()) {
                if (e.getValue() == null || (e.getValue() instanceof String && ((String) e.getValue()).equalsIgnoreCase(servername))) {
                    perms.add(e.getKey().replace('-', '.'));
                }
            }
            groups.put(g, perms);

            Set<Group> parentz = new HashSet<>();
            for (int id : entry.get("parents", new ArrayList<Integer>())) {
                parentz.add(Group.getGroupById(id));
            }
            parents.put(g, parentz);
        }

        for (Document entry : database.getCollection("permission_players").find()) {
            Set<String> perms = new HashSet<>();
            for (Map.Entry<String, Object> e : entry.get("permissions", new Document()).entrySet()) {
                if (e.getValue() == null || (e.getValue() instanceof String && ((String) e.getValue()).equalsIgnoreCase(servername))) {
                    perms.add(e.getKey().replace('-', '.'));
                }
            }
            permissions.put(UUID.fromString(entry.getString("uuid")), perms);
        }

        System.out.println(groups);
        System.out.println(parents);
        System.out.println(permissions);
    }

}

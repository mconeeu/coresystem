/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.api.core.player.Group;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.*;

public abstract class GlobalCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalCorePlayer {

    protected final GlobalCoreSystem instance;

    @Getter
    protected final String name;
    @Getter
    protected UUID uuid;
    private long onlinetime, joined;
    @Getter @Setter
    private boolean nicked;
    @Getter
    private Set<Group> groups;
    @Getter @Setter
    private Set<String> permissions;

    public GlobalCorePlayer(final GlobalCoreSystem instance, String name) throws PlayerNotFoundException {
        this.instance = instance;
        this.name = name;

        instance.getMySQL(1).select("SELECT uuid, groups, onlinetime FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.groups = instance.getPermissionManager().getGroups(rs.getString("groups"));
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (this.uuid == null) {
            this.uuid = instance.getPlayerUtils().fetchUuid(name);
            this.groups = new HashSet<>(Collections.singletonList(Group.SPIELER));
            this.onlinetime = 0;
            this.joined = System.currentTimeMillis() / 1000;
            throw new PlayerNotFoundException("Player is not listed in the database. Default values got loaded.");
        }
    }

    @Override
    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    @Override
    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

    @Override
    public void reloadPermissions() {
        permissions = instance.getPermissionManager().getPermissions(uuid.toString(), groups);
    }

    @Override
    public Group getMainGroup() {
        HashMap<Integer, Group> groups = new HashMap<>();
        this.groups.forEach(g -> groups.put(g.getId(), g));

        return Collections.min(groups.entrySet(), HashMap.Entry.comparingByValue()).getValue();
    }

    @Override
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
        reloadPermissions();
    }

    @Override
    public void addGroup(Group group) {
        this.groups.add(group);
        reloadPermissions();
    }

    @Override
    public void removeGroup(Group group) {
        this.groups.remove(group);
        reloadPermissions();
    }

}

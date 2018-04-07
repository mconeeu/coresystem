/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.lib.player.Group;
import eu.mcone.coresystem.lib.player.Skin;
import eu.mcone.coresystem.lib.util.UUIDFetcher;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.*;

public class CorePlayer {

    @Getter
    private UUID uuid;
    @Getter
    private String name, status;
    @Getter
    private Set<Group> groups;
    @Getter
    private long mutetime;
    @Getter @Setter
    private Set<String> permissions;
    @Getter
    private Map<UUID, String> friends;
    @Getter
    private Map<UUID, String> requests;
    @Getter
    private List<UUID> blocks;
    @Getter @Setter
    private boolean requestsToggled;
    private boolean isMuted = false;
    @Getter @Setter
    private Skin nick;
    @Getter @Setter
    private boolean nicked = false;

    public CorePlayer(String name) {
        CoreSystem.mysql1.select("SELECT uuid, groups, onlinetime FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.groups = Group.getGroups(rs.getString("groups"));
                } else {
                    this.uuid = UUIDFetcher.getUuid(name);
                    this.groups = new HashSet<>(Collections.singletonList(Group.SPIELER));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        CoreSystem.mysql1.select("SELECT `end` FROM `bungeesystem_bansystem_mute` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.isMuted = true;
                    this.mutetime = rs.getLong("end");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.name = name;
        this.status = "online";

        CoreSystem.getCorePlayers().put(uuid, this);
        reloadPermissions();

        Object[] friendData = CoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.requests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];

        System.out.println("Loaded Player "+name+"!");
    }

    public ProxiedPlayer bungee() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public void reloadPermissions() {
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groups);
    }

    public Group getMainGroup() {
        HashMap<Integer, Group> groups = new HashMap<>();
        this.groups.forEach(g -> groups.put(g.getId(), g));

        return Collections.min(groups.entrySet(), HashMap.Entry.comparingByValue()).getValue();
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
        reloadPermissions();
    }

    public void addGroup(Group group) {
        this.groups.add(group);
        reloadPermissions();
    }

    public void removeGroup(Group group) {
        this.groups.remove(group);
        reloadPermissions();
    }

    public boolean isMuted() {
        long millis = System.currentTimeMillis() / 1000;
        if (isMuted && mutetime < millis) {
            isMuted = false;
            ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                CoreSystem.mysql1.update("DELETE FROM `bungeesystem_bansystem_mute` WHERE end<"+millis);
            });
        }

        return isMuted;
    }

    public void unregister() {
        if (CoreSystem.getCorePlayers().containsKey(uuid)) CoreSystem.getCorePlayers().remove(uuid);
        System.out.println("Unloaded Player "+name+"!");
    }

}

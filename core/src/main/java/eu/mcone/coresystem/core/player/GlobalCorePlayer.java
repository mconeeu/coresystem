/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import lombok.Getter;
import lombok.Setter;
import net.labymod.serverapi.LabyModConnection;

import java.sql.SQLException;
import java.util.*;

public abstract class GlobalCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalCorePlayer {

    protected final GlobalCoreSystem instance;

    @Getter
    protected final String name;
    @Getter
    protected UUID uuid;
    @Getter
    private Language language;
    private long onlinetime, joined;
    @Getter @Setter
    private boolean nicked = false;
    @Getter
    private Set<Group> groups;
    @Getter @Setter
    private Set<String> permissions;
    @Getter @Setter
    private String teamspeakUid;
    @Getter @Setter
    private LabyModConnection labyModConnection;
    @Getter @Setter
    private PlayerSettings settings;

    protected GlobalCorePlayer(final GlobalCoreSystem instance, String name) throws PlayerNotFoundException {
        this.instance = instance;
        this.name = name;

        ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).select("SELECT uuid, groups, language, onlinetime, teamspeak_uid, player_settings FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.language = Language.valueOf(rs.getString("language"));
                    this.groups = instance.getPermissionManager().getGroups(rs.getString("groups"));
                    this.onlinetime = rs.getLong("onlinetime");
                    this.teamspeakUid = rs.getString("teamspeak_uid");
                    this.settings = ((CoreModuleCoreSystem) instance).getGson().fromJson(rs.getString("player_settings"), PlayerSettings.class);
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

    @Override
    public boolean isTeamspeakIdLinked() {
        return teamspeakUid != null;
    }

}

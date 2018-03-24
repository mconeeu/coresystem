/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.lib.exception.CoreException;
import eu.mcone.coresystem.lib.player.Group;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class CorePlayer {

    @Getter
    private UUID uuid;
    @Getter
    private String name, status;
    @Getter @Setter
    private String nickname;
    @Getter
    private Set<Group> groups;
    private long joined, onlinetime;
    @Getter
    private Set<String> permissions;
    @Getter
    private CoreScoreboard scoreboard;
    @Getter @Setter
    private boolean nicked;

    public CorePlayer(UUID uuid, String name) throws CoreException {
        this.uuid = uuid;
        this.name = name;
        this.status = "online";

        register();

        CoreSystem.mysql1.select("SELECT groups, onlinetime FROM userinfo WHERE uuid='"+uuid.toString()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                    this.groups = Group.getGroups(rs.getString("groups"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (this.groups == null) throw new CoreException("Database does not contain player "+name+"!");

        reloadPermissions();
        System.out.println("Loaded Player "+name+"!");
    }

    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public void setScoreboard(CoreScoreboard sb) {
        this.scoreboard = sb.set(this);
    }

    public void reloadPermissions() {
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groups);
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
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

    public void setStatus(final String status) {
        this.status = status;
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> CoreSystem.mysql1.update("UPDATE userinfo SET status='"+status+"' WHERE uuid='"+uuid+"'"));
    }

    private void register() {
        CoreSystem.getCorePlayers().put(uuid, this);
    }

    public void unregister() {
        CoreSystem.getInstance().clearPlayerInventories(uuid);
        AFKCheck.players.remove(uuid);
        AFKCheck.afkPlayers.remove(uuid);

        if (CoreSystem.getCorePlayers().containsKey(uuid)) CoreSystem.getCorePlayers().remove(uuid);
        System.out.println("Unloaded player "+name);
    }

}

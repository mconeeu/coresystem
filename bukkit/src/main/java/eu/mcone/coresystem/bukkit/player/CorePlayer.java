/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.scoreboard.Scoreboard;
import eu.mcone.coresystem.lib.player.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CorePlayer {

    private UUID uuid;
    private String name, status, nickname;
    private Group group;
    private long joined, onlinetime;
    private List<String> permissions;
    private Scoreboard scoreboard;
    private boolean nicked;

    public CorePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.status = "online";

        register();

        CoreSystem.mysql1.select("SELECT gruppe, onlinetime FROM userinfo WHERE uuid='"+uuid.toString()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                    this.group = Group.getGroupbyName(rs.getString("gruppe"));
                } else {
                    bukkit().kickPlayer("Du bist nicht im System registriert!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        reloadPermissions();
        System.out.println("Loaded Player "+name+"!");
    }

    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public void setScoreboard(Scoreboard sb) {
        this.scoreboard = sb;
    }

    public void reloadPermissions() {
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isNicked() {
        return nicked;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    public void setNicked(boolean nicked) {
        this.nicked = nicked;
    }

    public void setGroup(Group group) {
        this.group = group;
        permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }

    public void setStatus(final String status) {
        this.status = status;
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> CoreSystem.mysql1.update("UPDATE userinfo SET status='"+status+"' WHERE uuid='"+uuid+"'"));
    }

    private void register() {
        CoreSystem.getCorePlayers().put(uuid, this);
    }

    public void unregister() {
        if (CoreSystem.getCorePlayers().containsKey(uuid)) CoreSystem.getCorePlayers().remove(uuid);
        System.out.println("Unloaded player "+name);
    }

}

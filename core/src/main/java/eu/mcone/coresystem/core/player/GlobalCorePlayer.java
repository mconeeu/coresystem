/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.api.core.player.Group;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

public abstract class GlobalCorePlayer extends GlobalOfflineCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalCorePlayer {

    @Getter
    protected final String ipAdress;
    private long joined;
    @Getter @Setter
    private boolean nicked = false;
    @Getter @Setter
    private LabyModConnection labyModConnection;

    protected GlobalCorePlayer(final GlobalCoreSystem instance, final InetAddress address, UUID uuid, String name) {
        super(instance, uuid, name, true);
        this.ipAdress = address.toString().split("/")[1];
        this.joined = System.currentTimeMillis() / 1000;
    }

    protected GlobalCorePlayer(final GlobalCoreSystem instance, final InetAddress address, UUID uuid) throws PlayerNotResolvedException {
        super(instance, uuid, true);
        this.ipAdress = address.toString().split("/")[1];
        this.joined = System.currentTimeMillis() / 1000;
    }

    protected GlobalCorePlayer(final GlobalCoreSystem instance, final InetAddress address, String name) throws PlayerNotResolvedException {
        super(instance, name, true);
        this.ipAdress = address.toString().split("/")[1];
        this.joined = System.currentTimeMillis() / 1000;
    }

    @Override
    public long getOnlinetime() {
        return ((System.currentTimeMillis() / 1000) - joined) + onlinetime;
    }

    @Override
    public void addSemiPermission(String permission) {
        permissions.add(permission);
    }

    @Override
    public void removeSemiPermission(String permission) {
        permissions.remove(permission);
    }

    @Override
    public Set<Group> getGroups() {
        return groupSet;
    }

    @Override
    public Set<Group> updateGroupsFromDatabase() {
        Set<Group> result = super.updateGroupsFromDatabase();
        reloadPermissions();

        return result;
    }

}

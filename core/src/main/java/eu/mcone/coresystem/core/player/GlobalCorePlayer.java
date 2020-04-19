/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public abstract class GlobalCorePlayer extends GlobalOfflineCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalCorePlayer {

    @Getter
    protected final String ipAdress;
    private final long joined;
    @Getter @Setter
    private boolean nicked = false;
    @Getter @Setter
    private LabyModConnection labyModConnection;

    protected GlobalCorePlayer(final GlobalCoreSystem instance, final InetAddress address, UUID uuid, String name) {
        super(instance, uuid, name, true);

        if (!this.name.equals(name)) {
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§7Player §f"+name+"§7 has changed his name from §o"+this.name);

            this.name = name;
            instance.runAsync(() -> {
                ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                        eq("uuid", uuid.toString()),
                        set("name", name)
                );
                ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Updated name from player §a"+name+"§2 in database");
            });
        }
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

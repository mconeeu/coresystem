/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BungeeCorePlayer extends GlobalCorePlayer implements eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer {

    @Getter
    private final String status;
    @Getter
    private long muteTime;
    @Getter
    private Map<UUID, String> friends;
    @Getter
    private Map<UUID, String> friendRequests;
    @Getter
    private List<UUID> blocks;
    @Getter @Setter
    private boolean requestsToggled;
    private boolean muted = false;
    @Getter @Setter
    private SkinInfo nickedSkin;

    public BungeeCorePlayer(CoreSystem instance, String name) throws PlayerNotFoundException {
        super(instance, name);

        ((BungeeCoreSystem) instance).getMySQL(Database.SYSTEM).select("SELECT `end` FROM `bungeesystem_bansystem_mute` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.muted = true;
                    this.muteTime = rs.getLong("end");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.status = "online";

        ((BungeeCoreSystem) instance).getCorePlayers().put(uuid, this);
        reloadPermissions();

        Object[] friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.friendRequests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];

        CoreSystem.getInstance().sendConsoleMessage("Loaded Player "+name+"!");
    }

    @Override
    public ProxiedPlayer bungee() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public boolean isMuted() {
        long millis = System.currentTimeMillis() / 1000;
        if (muted && muteTime < millis) {
            muted = false;
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                ((BungeeCoreSystem) instance).getMySQL(Database.SYSTEM).update("DELETE FROM `bungeesystem_bansystem_mute` WHERE end<"+millis);
            });
        }

        return muted;
    }

    @Override
    public void sendMessage(String message) {
        bungee().sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void unregister() {
        BungeeCoreSystem.getSystem().getCorePlayers().remove(uuid);
        CoreSystem.getInstance().sendConsoleMessage("Unloaded Player "+name+"!");
    }

}

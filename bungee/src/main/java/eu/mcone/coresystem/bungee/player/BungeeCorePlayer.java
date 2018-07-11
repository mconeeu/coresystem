/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.FriendData;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.sql.SQLException;

public class BungeeCorePlayer extends GlobalCorePlayer implements CorePlayer {

    @Getter
    private long muteTime;
    @Getter
    private FriendData friendData;
    private boolean muted = false;
    @Getter @Setter
    private SkinInfo nickedSkin;

    public BungeeCorePlayer(CoreSystem instance, InetAddress address, String name) throws PlayerNotResolvedException {
        super(instance, address, name);

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

        ((BungeeCoreSystem) instance).getCorePlayers().put(uuid, this);
        reloadPermissions();

        this.friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);

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
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () ->
                    ((BungeeCoreSystem) instance).getMySQL(Database.SYSTEM).update("DELETE FROM `bungeesystem_bansystem_mute` WHERE end<"+millis));
        }

        return muted;
    }

    @Override
    public void updateSettings() {
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, settings));
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(bungee(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));

        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update(
                "UPDATE userinfo SET player_settings='"+((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class)+"' WHERE uuid ='"+uuid+"'"
        );
    }

    @Override
    public boolean isNew() {
        return isNew;
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

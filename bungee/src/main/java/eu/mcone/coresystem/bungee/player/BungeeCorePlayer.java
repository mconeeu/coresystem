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
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.net.InetAddress;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Updates.set;

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

        Document entry = ((BungeeCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            this.muted = true;
            this.muteTime = entry.getLong("end");
        }

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
        if (muted && muteTime <= millis) {
            muted = false;
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () ->
                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").deleteMany(lte("end", millis))
            );
        }

        return muted;
    }

    @Override
    public void updateSettings() {
        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, settings));
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(bungee(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));

        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid), set("player_settings", Document.parse(((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class))));
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

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.player.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.player.PlayerVanishEvent;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.api.bukkit.stats.CoreStats;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.Region;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.channel.packet.PacketInListenerImpl;
import eu.mcone.coresystem.bukkit.channel.packet.PacketOutListenerImpl;
import eu.mcone.coresystem.bukkit.listener.CorePlayerListener;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.net.InetAddress;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BukkitCorePlayer extends GlobalCorePlayer implements CorePlayer, OfflineCorePlayer {

    @Getter
    @Setter
    private Nick nick;
    @Getter
    private CoreScoreboard scoreboard;
    private final Map<Gamemode, CoreStats> stats;
    @Getter
    private final SkinInfo skin;
    @Getter
    private boolean vanished;
    private PacketInListenerImpl packetInListener;
    private PacketOutListenerImpl packetOutListener;
    @Getter
    private PermissionAttachment permissionAttachment;

    public BukkitCorePlayer(CoreSystem instance, InetAddress address, SkinInfo skinInfo, Player p) {
        super(instance, address, p.getUniqueId(), p.getName());
        this.stats = new HashMap<>();
        this.skin = skinInfo;
        this.vanished = false;

        updatePermissionAttachment(p);

        instance.runAsync(() -> ((BukkitCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                eq("uuid", this.uuid.toString()),
                combine(
                        set("texture_value", skinInfo.getValue()),
                        set("texture_signature", skinInfo.getSignature())
                )
        ));

        updateTrust();

        ((BukkitCoreSystem) instance).getCorePlayers().put(uuid, this);
        BukkitCoreSystem.getInstance().sendConsoleMessage("Loaded Player " + name + "!");
    }

    @Override
    public void reloadPermissions() {
        super.reloadPermissions();

        if (permissionAttachment != null) {
            permissionAttachment.remove();
        }

        Player p = bukkit();
        if (p != null) {
            updatePermissionAttachment(p);
        }
    }

    @Override
    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void setScoreboard(CoreScoreboard sb) {
        if (this.scoreboard != null) scoreboard.unregister();
        this.scoreboard = sb.set(this);
    }

    @Override
    public CoreWorld getWorld() {
        return BukkitCoreSystem.getInstance().getWorldManager().getWorld(bukkit().getWorld());
    }

    @Override
    public <S> S getStats(Gamemode gamemode, Class<S> clazz) {
        S stats;
        if (this.stats.containsKey(gamemode)) {
            if (this.stats.get(gamemode).getClass().isAssignableFrom(clazz)) {
                stats = (S) this.stats.get(gamemode);
            } else {
                stats = CoreSystem.getInstance().getCoreStatsManager().getStats(gamemode, uuid, clazz);
            }
        } else {
            stats = CoreSystem.getInstance().getCoreStatsManager().getStats(gamemode, uuid, clazz);
        }

        return stats;
    }

    @Override
    public void teleportWithCooldown(Location location, int cooldown) {
        Player p = bukkit();

        if (cooldown > 0) {
            BukkitCoreSystem.getSystem().getMessenger().send(p, "§7Du wirst in §f" + cooldown + " Sekunden§7 teleportiert! Bewege dich nicht!");
            CorePlayerListener.TELEPORTS.put(uuid, Bukkit.getScheduler().runTaskLater(BukkitCoreSystem.getSystem(), () -> {
                CorePlayerListener.TELEPORTS.remove(uuid);

                p.teleport(location);
            }, cooldown * 20));
        } else {
            p.teleport(location);
        }
    }

    @Override
    public boolean isAfk() {
        return BukkitCoreSystem.getSystem().getAfkManager().isAfk(uuid);
    }

    @Override
    public long getAfkTime() {
        return BukkitCoreSystem.getSystem().getAfkManager().getAfkTime(uuid);
    }

    @Override
    public void updateSettings() {
        Bukkit.getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, settings));
        CoreSystem.getInstance().getChannelHandler().createSetRequest(bukkit(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));
        BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("player_settings", Document.parse(((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class))));
    }

    @Override
    public void sendMessage(String message) {
        bukkit().sendMessage(message);
    }

    @Override
    public boolean setVanished(boolean vanish) {
        if (vanished != vanish) {
            vanished = vanish;

            PlayerVanishEvent vanishEvent = new PlayerVanishEvent(this, vanish);
            Bukkit.getPluginManager().callEvent(vanishEvent);

            if (!vanishEvent.isCancelled()) {
                Player p = bukkit();

                if (vanish) {
                    BukkitCoreSystem.getSystem().getVanishManager().recalculateVanishes();

                    for (CorePlayer t : CoreSystem.getInstance().getOnlineCorePlayers()) {
                        if (t.hasPermission("system.bukkit.vanish.see") || t.bukkit() == p) {
                            CoreScoreboard sb = t.getScoreboard();

                            if (sb instanceof MainScoreboard) {
                                sb.reload();
                            }
                        }
                    }

                    Sound.done(p);
                    BukkitCoreSystem.getInstance().getMessenger().send(bukkit(), "§2Du bist nun im §aVanish Modus§2!");
                } else {
                    BukkitCoreSystem.getSystem().getVanishManager().recalculateVanishes();

                    for (Player t : Bukkit.getOnlinePlayers()) {
                        CoreScoreboard sb = CoreSystem.getInstance().getCorePlayer(t).getScoreboard();

                        if (sb instanceof MainScoreboard) {
                            sb.reload();
                        }
                    }

                    Sound.done(p);
                    BukkitCoreSystem.getInstance().getMessenger().send(bukkit(), "§7Du bist nicht mehr im §fVanish Modus§7!");
                }

                return true;
            } else {
                vanished = !vanish;
                if (!vanishEvent.getCancelCause().isEmpty()) {
                    BukkitCoreSystem.getInstance().getMessenger().send(bukkit(), vanishEvent.getCancelCause());
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public Set<Region> getApplicableRegions() {
        List<Region> worldRegions = getWorld().getRegions();

        if (worldRegions.size() > 0) {
            Set<Region> regions = new HashSet<>();
            Location loc = bukkit().getLocation();

            for (Region region : worldRegions) {
                if (region.isInRegion(loc)) {
                    regions.add(region);
                }
            }

            return regions;
        }

        return Collections.emptySet();
    }

    public void registerPacketListener(Player p) {
        this.packetInListener = new PacketInListenerImpl(p);
        this.packetOutListener = new PacketOutListenerImpl(p);
    }

    public void unregisterAttachment() {
        if (permissionAttachment != null) bukkit().removeAttachment(permissionAttachment);
    }

    public void unregister() {
        scoreboard.unregister();
        BukkitCoreSystem.getSystem().getAfkManager().unregisterPlayer(uuid);

        if (packetInListener != null) {
            packetInListener.remove();
        }

        BukkitCoreSystem.getInstance().sendConsoleMessage("Unloaded Player " + name);
    }

    private void updatePermissionAttachment(Player p) {
        permissionAttachment = p.addAttachment((Plugin) instance);

        for (String permission : permissions) {
            permissionAttachment.setPermission(permission.startsWith("-") ? permission.substring(1) : permission, !permission.startsWith("-"));
        }
    }

}

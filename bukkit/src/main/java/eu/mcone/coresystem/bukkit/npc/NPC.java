/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC implements eu.mcone.coresystem.api.bukkit.npc.NPC {

    @Getter
    private List<UUID> loadedPlayers;
    @Getter
    private Location location;
    @Getter
    private SkinInfo skin;
    @Getter
    private EntityPlayer entity;
    @Getter
    private UUID uuid;
    @Getter
    private String name, displayname;

    public NPC(String name, Location location, SkinInfo skin, String displayname){
        this.name = name;
        this.displayname = displayname;
        this.uuid = UUID.randomUUID();
        this.location = location;
        this.loadedPlayers = new ArrayList<>();
        this.skin = skin;

        createProfile();
    }

    public NPC(String name, Location location, String skinName, String displayname){
        try {
            this.name = name;
            this.displayname = displayname;
            this.uuid = UUID.randomUUID();
            this.location = location;
            this.loadedPlayers = new ArrayList<>();
            this.skin = new eu.mcone.coresystem.core.player.SkinInfo(BukkitCoreSystem.getSystem().getMySQL(Database.SYSTEM), skinName).downloadSkinData();

            createProfile();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private void createProfile() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        GameProfile gameprofile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', displayname));
        gameprofile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        entity = new EntityPlayer(server, world, gameprofile, new PlayerInteractManager(world));

        entity.playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NetworkManager(EnumProtocolDirection.SERVERBOUND), entity);
        entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entity);
    }

    public void set(Player p) {
        DataWatcher watcher = entity.getDataWatcher();
        watcher.watch(10, (byte) 127);

        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, ((byte) (int) (location.getYaw()*256.0F/360.0F))));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte) location.getYaw(), (byte) location.getPitch(), false));
        connection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), watcher, true));
        entity.getBukkitEntity().getPlayer().setPlayerListName("");

        loadedPlayers.add(p.getUniqueId());

        Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitCoreSystem.getInstance(), () ->
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity))
        , 40L);
    }

    public void setSkin(SkinInfo skin, Player p) {
        GameProfile gp = entity.getProfile();

        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));

        set(p);

        Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            gp.getProperties().removeAll("textures");
            gp.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
        }, 50);
    }

    public void setName(String displayname, Player p) {
        PropertyMap oldProperties = entity.getProfile().getProperties();

        GameProfile gp = new GameProfile(uuid, displayname);
        gp.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
    }

    public void unset(Player p) {
        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));

        loadedPlayers.remove(p.getUniqueId());
    }

    public void unsetAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unset(p);
        }
    }

    public void destroy() {
        ((CraftWorld) location.getWorld()).getHandle().removeEntity(entity);
    }

}

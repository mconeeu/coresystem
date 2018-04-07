/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.lib.exception.CoreException;
import eu.mcone.coresystem.lib.player.Skin;
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

public class NPC {

    @Getter
    private List<UUID> loadedPlayers;
    @Getter
    private Location location;
    @Getter
    private Skin skin;
    @Getter
    private EntityPlayer npc;
    @Getter
    private UUID uuid;
    @Getter
    private String name, displayname;

    public NPC(String name, Location location, String skinName, String displayname){
        try {
            this.name = name;
            this.displayname = displayname;
            this.uuid = UUID.randomUUID();
            this.location = location;
            this.loadedPlayers = new ArrayList<>();
            this.skin = new Skin(CoreSystem.mysql1, skinName).downloadSkinData();

            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

            GameProfile gameprofile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', displayname));
            gameprofile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

            npc = new EntityPlayer(server, world, gameprofile, new PlayerInteractManager(world));

            npc.playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NPCNetworkManager(), npc);
            npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            ((CraftWorld) location.getWorld()).getHandle().addEntity(npc);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void set(Player p) {
        DataWatcher watcher = npc.getDataWatcher();
        watcher.watch(10, (byte) 127);

        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, ((byte) (int) (location.getYaw()*256.0F/360.0F))));
        npc.getBukkitEntity().getPlayer().setPlayerListName("");

        loadedPlayers.add(p.getUniqueId());

        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreSystem.getInstance(), () ->
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc))
        , 26);
    }

    public void setSkin(Skin skin, Player p) {
        GameProfile gp = npc.getProfile();

        gp.getProperties().removeAll("textures");
        gp.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));

        set(p);

        Bukkit.getScheduler().runTaskLaterAsynchronously(CoreSystem.getInstance(), () -> {
            gp.getProperties().removeAll("textures");
            gp.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
        }, 50);
    }

    public void setName(String displayname, Player p) {
        PropertyMap oldProperties = npc.getProfile().getProperties();

        GameProfile gp = new GameProfile(uuid, displayname);
        gp.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
    }

    public void unset(Player p) {
        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        if (loadedPlayers.contains(p.getUniqueId())) loadedPlayers.remove(p.getUniqueId());
    }

    public void unsetAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unset(p);
        }
    }

    public void destroy() {
        ((CraftWorld) location.getWorld()).getHandle().removeEntity(npc);
    }

}

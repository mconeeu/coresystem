/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.bukkit.CoreSystem;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private List<UUID> loadedPlayers;
    private Location loc;
    private EntityPlayer npc;
    private UUID uuid;
    private String displayname;

    public NPC(String name, Location loc, String data, String displayname){
        this.displayname = name;
        this.uuid = UUID.randomUUID();
        this.loc = loc;
        this.loadedPlayers = new ArrayList<>();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        GameProfile gameprofile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', displayname));
        CoreSystem.mysql1.select("SELECT * FROM bukkitsystem_textures WHERE name='"+data+"'", rs -> {
            try {
                if (rs.next()) {
                    String texture = rs.getString("texture_value");
                    String signature = rs.getString("texture_signature");
                    gameprofile.getProperties().put("textures", new Property("textures", texture, signature));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        npc = new EntityPlayer(server, world, gameprofile, new PlayerInteractManager(world));

        npc.playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NPCNetworkManager(), npc);
        npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(npc);
    }

    public void set(Player p) {
        DataWatcher watcher = npc.getDataWatcher();
        watcher.watch(10, (byte) 127);

        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, ((byte) (int) (loc.getYaw()*256.0F/360.0F))));
        npc.getBukkitEntity().getPlayer().setPlayerListName("");

        loadedPlayers.add(p.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
            }
        }.runTaskLater(CoreSystem.getInstance(), 20);
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
        ((CraftWorld) loc.getWorld()).getHandle().removeEntity(npc);
    }

    public Location getLoc() {
        return loc;
    }

    public List<UUID> getLoadedPlayers() {
        return loadedPlayers;
    }

    public String getDisplayname() {
        return displayname;
    }

    public UUID getUuid() {
        return uuid;
    }

}

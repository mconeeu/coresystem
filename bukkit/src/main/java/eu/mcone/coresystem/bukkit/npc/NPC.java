/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC implements eu.mcone.coresystem.api.bukkit.npc.NPC, Listener {

    @Getter
    private List<UUID> loadedPlayers;
    @Getter
    private NpcData data;
    @Getter
    private CoreWorld world;
    @Getter
    private SkinInfo skin;
    @Getter
    private EntityPlayer entity;
    @Getter
    private UUID uuid;
    @Getter
    @Setter
    private boolean local = false;

    public NPC(CoreWorld world, NpcData data) throws NpcCreateException {
        this.data = data;
        this.world = world;
        this.uuid = UUID.randomUUID();
        this.loadedPlayers = new ArrayList<>();

        try {
            switch (data.getSkinKind()) {
                case DATABASE: this.skin = CoreSystem.getInstance().getDatabaseSkinManager().getSkin(data.getSkinName()); break;
                case PLAYER: this.skin = CoreSystem.getInstance().getPlayerUtils().getSkinInfo(data.getSkinName()); break;
            }
        } catch (SkinNotFoundException e) {
            this.skin = new SkinInfo(
                    "?",
                    "eyJ0aW1lc3RhbXAiOjE1MTIyNjI0NTg5NDcsInByb2ZpbGVJZCI6ImQxY2VjOWFkMWRhODQxNzliMWU1NjA0ZjcyYmZiMjI2IiwicHJvZmlsZU5hbWUiOiJydXRnZXI0NjUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQyNDQzNjg4ZGI5NjQ5YjA1NTliNjg4ZTdjMTI3ZjY2N2FiYjhmOWY1YTU5ZDVhOWRhNzEyMjZmNzNmODMzMCJ9fX0=",
                    "iVxHKJfmQh8VlCXF0Wwr6Yl6p81+OIXuGLoM/KM1sgM+OQ2WwHr7V6FNHATnlz1uGEDpzMZUKsUq9SVy50soQ9RWb7iynfB51QBxU0NByUCNQf2olHXEZH6CQEUbZTgYrN1aNZ1Y/Z7T1xrMKfc43HqNfM2H49JufRoLr4ZPWcl6T8d0lSzshndkFZxtS45PRQDBzo0QFyG63WjeoMbW42ufaTWVYz354BhnksAQWb1lSfdXcB7JQOKjf0MzeYmx0pOMB5CWhERJQZpi5mJ1MoabeSwNygcdfZAMB9xtmScbX5tUPvrC2Ooo20jl2fQ/KG+6obZeydKKr2vznj+0oq+04VnmqooHhLjXyZSvGIB3Ht9aDL9MzVbwpbLnLjrngyUzX/7+oJEm2a4xrktszoHGKHdnbD1d9CDyWh+FnyRCmO5RE3ZR8yHE5SakTKOkASR2H79RAHn+wF1h535wJctjoHyYBRc/gXWo58jyjG1hGu/6cI2XuQCEvdnkZTj0rqKWUtn8pWn2KLbm2S2S6CoTMTsGHG0anz4c4Jkzni91wzWvg5xIf1f3YzMRhuyOirtM9gyJok2BplRaGiv1f63fWa7cbS6A3nj/sKr2SNQ6Efri6+Z1xPOOy7xHFaZH0wlf8kz1qPDqIw/nAy0eZAthPN61TZw5iCegIG01KqU="
            );

            throw new NpcCreateException("Could not create NPC: ", e);
        }

        createProfile();
    }

    private void createProfile() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        GameProfile gameprofile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', data.getDisplayname()));
        gameprofile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        entity = new EntityPlayer(server, world, gameprofile, new PlayerInteractManager(world));

        entity.playerConnection = new PlayerConnection(MinecraftServer.getServer(), new NetworkManager(EnumProtocolDirection.SERVERBOUND), entity);
        entity.setPositionRotation(data.getLocation().getX(), data.getLocation().getY(), data.getLocation().getZ(), (byte) data.getLocation().getYaw(), (byte) data.getLocation().getPitch());
        ((CraftWorld) this.world.bukkit()).getHandle().addEntity(entity);
    }

    public void set(Player p) {
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
        entity.getBukkitEntity().getPlayer().setPlayerListName("");

        loadedPlayers.add(p.getUniqueId());

        Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            DataWatcher watcher = entity.getDataWatcher();
            watcher.watch(10, (byte) 127);

            connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, ((byte) (int) (data.getLocation().getYaw() * 256.0F / 360.0F))));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte) data.getLocation().getYaw(), (byte) data.getLocation().getPitch(), false));
            connection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), watcher, true));
        }, 5);
        Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitCoreSystem.getInstance(), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity)), 10L);
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

    public void unset(Player p) {
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));

        loadedPlayers.remove(p.getUniqueId());
    }

    public void unsetAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unset(p);
        }
    }

    @Override
    public Location getLocation() {
        return data.getLocation().bukkit(world);
    }

    public void destroy() {
        ((CraftWorld) world.bukkit()).getHandle().removeEntity(entity);
    }

}

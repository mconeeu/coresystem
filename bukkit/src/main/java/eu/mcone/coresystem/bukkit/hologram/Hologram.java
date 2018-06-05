/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram implements eu.mcone.coresystem.api.bukkit.hologram.Hologram {

    private List<EntityArmorStand> entitylist;
    private String[] Text;
    @Getter
    private Location location;
    private double DISTANCE;
    @Getter
    private int count;
    @Getter
    private List<Player> playerList;

    public Hologram(String[] Text, final Location location) {
        this.entitylist = new ArrayList<>();
        this.playerList = new ArrayList<>();
        this.DISTANCE = 0.25;
        this.Text = Text;
        this.location = location;
        this.create();
    }

    public void showPlayerTemp(final Player p, final int Time) {
        this.showPlayer(p);
        Bukkit.getScheduler().runTaskLater(BukkitCoreSystem.getInstance(), () -> Hologram.this.hidePlayer(p), (long)Time);
    }

    public void showAllTemp(final int Time) {
        this.showAll();
        Bukkit.getScheduler().runTaskLater(BukkitCoreSystem.getInstance(), Hologram.this::hideAll, (long)Time);
    }

    public void showPlayer(final Player p) {
        if (!playerList.contains(p)) {
            for (final EntityArmorStand armor : this.entitylist) {
                final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armor);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
            playerList.add(p);
        }
    }

    public void hidePlayer(final Player p) {
        if (playerList.contains(p)) {
            for (final EntityArmorStand armor : this.entitylist) {
                final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armor.getId());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
            playerList.remove(p);
        }
    }

    public void showAll() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            showPlayer(p);
        }
    }

    public void hideAll() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            hidePlayer(p);
        }
    }

    private void create() {
        String[] text;
        for (int length = (text = this.Text).length, j = 0; j < length; ++j) {
            final String Text = text[j];
            final EntityArmorStand entity = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle(), this.location.getX(), this.location.getY()-1.8, this.location.getZ());
            entity.setCustomName(Text);
            entity.setCustomNameVisible(true);
            entity.setInvisible(true);
            entity.setGravity(false);
            this.entitylist.add(entity);
            this.location.subtract(0.0, this.DISTANCE, 0.0);
            ++this.count;
        }
        for (int i = 0; i < this.count; ++i) {
            this.location.add(0.0, this.DISTANCE, 0.0);
        }
        this.count = 0;
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CoreHologram implements eu.mcone.coresystem.api.bukkit.hologram.Hologram {

    private static final double DISTANCE = 0.25;

    private final List<EntityArmorStand> entitylist;
    private List<Player> playerList;

    @Getter
    private HologramData data;

    public CoreHologram(HologramData data) {
        this.entitylist = new ArrayList<>();
        this.playerList = new ArrayList<>();

        this.data = data;
        this.create();
    }

    @Override
    public void showPlayerTemp(final Player p, final int Time) {
        this.showPlayer(p);
        Bukkit.getScheduler().runTaskLater(BukkitCoreSystem.getInstance(), () -> CoreHologram.this.hidePlayer(p), (long)Time);
    }

    @Override
    public void showAllTemp(final int Time) {
        this.showAll();
        Bukkit.getScheduler().runTaskLater(BukkitCoreSystem.getInstance(), CoreHologram.this::hideAll, (long)Time);
    }

    @Override
    public void showPlayer(final Player p) {
        if (!playerList.contains(p)) {
            for (final EntityArmorStand armor : this.entitylist) {
                final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armor);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
            playerList.add(p);
        }
    }

    @Override
    public void hidePlayer(final Player p) {
        if (playerList.contains(p)) {
            for (final EntityArmorStand armor : this.entitylist) {
                final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armor.getId());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
            playerList.remove(p);
        }
    }

    @Override
    public void showAll() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            showPlayer(p);
        }
    }

    @Override
    public void hideAll() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            hidePlayer(p);
        }
    }

    private void create() {
        Location loc = data.getLocation().bukkit();
        int count = 0;

        String[] text;
        for (int length = (text = data.getText()).length, j = 0; j < length; ++j) {
            final String Text = text[j];
            final EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY()-1.8, loc.getZ());
            entity.setCustomName(Text);
            entity.setCustomNameVisible(true);
            entity.setInvisible(true);
            entity.setGravity(false);
            this.entitylist.add(entity);
            loc.subtract(0.0, DISTANCE, 0.0);
            ++count;
        }
        for (int i = 0; i < count; ++i) {
            loc.add(0.0, DISTANCE, 0.0);
        }
    }

}

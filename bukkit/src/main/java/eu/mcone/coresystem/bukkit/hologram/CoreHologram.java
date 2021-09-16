/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.bukkit.util.PlayerListModeToggleUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CoreHologram extends PlayerListModeToggleUtil implements eu.mcone.coresystem.api.bukkit.hologram.Hologram {

    private static final double DISTANCE = 0.25;

    private final List<EntityArmorStand> entitylist;
    @Getter
    private final HologramData data;

    public CoreHologram(HologramData data, ListMode listMode, Player... players) {
        this.entitylist = new ArrayList<>();
        this.data = data;

        create();
        togglePlayerVisibility(listMode, players);
    }

    @Override
    public void spawn(final Player p) {
        for (final EntityArmorStand armor : this.entitylist) {
            final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armor);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void despawn(final Player p) {
        for (final EntityArmorStand armor : this.entitylist) {
            final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armor.getId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void update(HologramData data) {
        this.data.setLocation(data.getLocation());
        this.data.setText(data.getText());

        for (Player p : visiblePlayersList) {
            despawn(p);
        }
        create();
        for (Player p : visiblePlayersList) {
            if (canBeSeenBy(p)) {
                spawn(p);
            }
        }
    }

    @Override
    public void playerJoined(Player... players) {
        super.playerJoined(players);
        for (Player player : players) {
            if (visiblePlayersList.contains(player) && canBeSeenBy(player)) {
                spawn(player);
            }
        }
    }

    @Override
    public boolean canBeSeenBy(Player p) {
        return p.getWorld().equals(data.getLocation().bukkit().getWorld());
    }

    private void create() {
        entitylist.clear();
        Location loc = data.getLocation().bukkit();
        int count = 0;

        String[] text = data.getText();
        for (final String line : text) {
            final EntityArmorStand entity = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY() - 1.8, loc.getZ());
            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', line));
            entity.setCustomNameVisible(true);
            entity.setInvisible(true);
            entity.setGravity(false);
            entity.noclip = true;

            this.entitylist.add(entity);
            loc.subtract(0.0, DISTANCE, 0.0);
            ++count;
        }

        for (int i = 0; i < count; ++i) {
            loc.add(0.0, DISTANCE, 0.0);
        }
    }

}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

@NoArgsConstructor
@BsonDiscriminator
public final class CoreLocation implements Serializable {

    @BsonIgnore
    private transient Location bukkit;

    @Getter
    @Setter
    private String world;
    @Getter
    @Setter
    private double x, y, z;
    @Getter
    @Setter
    private float yaw, pitch;

    @BsonCreator
    public CoreLocation(@BsonProperty("world") String world, @BsonProperty("x") double x, @BsonProperty("y") double y, @BsonProperty("z") double z,
                        @BsonProperty("yaw") float yaw, @BsonProperty("pitch") float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public CoreLocation(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    @BsonIgnore
    public Location bukkit() {
        return bukkit != null ? bukkit : (bukkit = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    @Override
    public String toString() {
        return "CoreLocation{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}

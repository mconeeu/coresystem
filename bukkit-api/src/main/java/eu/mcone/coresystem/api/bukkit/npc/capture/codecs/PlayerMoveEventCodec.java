/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.*;

@Getter
public class PlayerMoveEventCodec extends Codec<PlayerMoveEvent, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PlayerMoveEventCodec() {
        super((byte) 1, (byte) 2);
    }

    @Override
    public Object[] decode(Player player, PlayerMoveEvent event) {
        this.x = event.getTo().getX();
        this.y = event.getTo().getY();
        this.z = event.getTo().getZ();
        this.yaw = event.getTo().getYaw();
        pitch = event.getTo().getPitch();

        return new Object[]{event.getPlayer()};
    }

    @Override
    public void encode(PlayerNpc npc) {
        Location location = npc.getLocation();
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        npc.teleport(location);
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        yaw = in.readFloat();
        pitch = in.readFloat();
    }

    @Override
    public String toString() {
        return "PlayerMoveEventCodec{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import com.google.common.io.ByteArrayDataOutput;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

@Getter
public class PlayerMoveEventCodec extends Codec<PlayerMoveEvent> {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PlayerMoveEventCodec() {
        super("MOVE");
    }

    @Override
    public void decode(Player player, PlayerMoveEvent event) {
        this.x = event.getTo().getX();
        this.y = event.getTo().getY();
        this.z = event.getTo().getZ();
        this.yaw = event.getTo().getYaw();
        pitch = event.getTo().getPitch();
    }

    @Override
    public List<Packet<?>> encode(Object object) {
        if (object instanceof PlayerNpc) {
            PlayerNpc npc = (PlayerNpc) object;
            Location location = npc.getLocation();
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            location.setYaw(yaw);
            location.setPitch(pitch);
            npc.teleport(location);
        }

        return null;
    }

    @Override
    public void onWrite(ByteArrayDataOutput out) {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
    }

    @Override
    public void onRead(ObjectInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        yaw = in.readFloat();
        pitch = in.readFloat();
    }

    @Override
    public Class<PlayerMoveEvent> getCodecClass() {
        return PlayerMoveEvent.class;
    }
}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import com.google.common.io.ByteArrayDataOutput;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class PlayInEntityActionCodec extends Codec<PacketPlayInEntityAction> {

    private String action;
    //Only used for horse jumps
    private int index;

    public PlayInEntityActionCodec() {
        super("ENTITY_ACTION");
    }

    @Override
    public void decode(Player player, PacketPlayInEntityAction packet) {
        action = packet.b().toString();
        index = packet.c();
    }

    @Override
    public List<Packet<?>> encode(Object object) {
        if (object instanceof PlayerNpc) {
            PlayerNpc npc = (PlayerNpc) object;
            PacketPlayInEntityAction.EnumPlayerAction action = PacketPlayInEntityAction.EnumPlayerAction.valueOf(this.action);
            switch (action) {
                case START_SNEAKING:
                    npc.sneak(true);
                    break;
                case STOP_SNEAKING:
                    npc.sneak(false);
                    break;
            }
        }

        return null;
    }

    @Override
    public void onWrite(ByteArrayDataOutput out) throws IOException {
        out.writeUTF(action);
        out.writeInt(index);
    }

    @Override
    public void onRead(ObjectInputStream in) throws IOException {
        action = in.readUTF();
        index = in.readInt();
    }

    @Override
    public Class<PacketPlayInEntityAction> getCodecClass() {
        return PacketPlayInEntityAction.class;
    }
}

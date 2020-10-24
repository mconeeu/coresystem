/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayInEntityActionCodec extends Codec<PacketPlayInEntityAction, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private EnumPlayerAction action;
    //Only used for horse jumps
    private int index;

    public PlayInEntityActionCodec() {
        super((short) 5, (short) 2);
    }

    @Override
    public Object[] decode(Player player, PacketPlayInEntityAction packet) {
        action = EnumPlayerAction.valueOf(packet.b().toString());
        index = packet.c();

        return new Object[]{player};
    }

    @Override
    public void encode(PlayerNpc npc) {
        switch (action) {
            case START_SNEAKING:
                npc.sneak(true);
                break;
            case STOP_SNEAKING:
                npc.sneak(false);
                break;
        }
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeByte(action.getId());
        out.writeByte(index);
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        action = EnumPlayerAction.getActionWhereID(in.readByte());
        index = in.readByte();
    }

    @Override
    public String toString() {
        return "PlayInEntityActionCodec{" +
                "action=" + action +
                ", index=" + index +
                '}';
    }
}

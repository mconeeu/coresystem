/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayInEntityActionCodec extends Codec<PacketPlayInEntityAction, PlayerNpc> {

    private String action;
    //Only used for horse jumps
    private int index;

    public PlayInEntityActionCodec() {
        super("ENTITY_ACTION");
    }

    @Override
    public Object[] decode(Player player, PacketPlayInEntityAction packet) {
        action = packet.b().toString();
        index = packet.c();

        return new Object[]{player};
    }

    @Override
    public void encode(PlayerNpc npc) {
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

    @Override
    public void onWriteObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(action);
        out.writeInt(index);
    }

    @Override
    public void onReadObject(ObjectInputStream in) throws IOException {
        action = in.readUTF();
        index = in.readInt();
    }
}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayOutAnimationCodec extends Codec<PacketPlayOutAnimation, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private NpcAnimation animation;

    public PlayOutAnimationCodec() {
        super((short) 6, (short) 2);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutAnimation packet) {
        int id = ReflectionManager.getValue(packet, "b", Integer.class);
        NpcAnimation animation = NpcAnimation.getAnimation((byte) id);
        if (animation != null) {
            this.animation = animation;
        }

        return new Object[]{player};
    }

    @Override
    public void encode(PlayerNpc npc) {
        npc.sendAnimation(animation);
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeByte(animation.getId());
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        animation = NpcAnimation.getAnimation(in.readByte());
    }

    @Override
    public String toString() {
        return "PlayOutAnimationCodec{" +
                "animation=" + animation +
                '}';
    }
}

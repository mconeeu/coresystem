/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayOutAnimationCodec extends Codec<PacketPlayOutAnimation, PlayerNpc> {

    private NpcAnimation animation;

    public PlayOutAnimationCodec() {
        super("DAMAGE", PacketPlayOutAnimation.class, PlayerNpc.class);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutAnimation packet) {
        int id = ReflectionManager.getValue(packet, "b", Integer.class);
        NpcAnimation animation = NpcAnimation.getAnimation(id);
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
    public void onWriteObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(animation.toString());
    }

    @Override
    public void onReadObject(ObjectInputStream in) throws IOException {
        animation = NpcAnimation.valueOf(in.readUTF());
    }
}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
public class BlockSound {

    private final Block block;
    private final net.minecraft.server.v1_8_R3.Block nmsBlock;

    public BlockSound(Block block) {
        this.block = block;
        this.nmsBlock = ((CraftWorld) block.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ())).getBlock();
    }

    public void playSound(SoundKey key) {
        playSound(key, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public void playSound(SoundKey key, Player[] players) {
        sendNearBy(key, players);
    }

    private void sendNearBy(SoundKey key, Player[] players) {
        double x = block.getLocation().getX();
        double y = block.getLocation().getY();
        double z = block.getLocation().getZ();

        for (Player player : players) {
            double d4 = x - player.getLocation().getX();
            double d5 = y - player.getLocation().getY();
            double d6 = z - player.getLocation().getZ();
            if (d4 * d4 + d5 * d5 + d6 * d6 < 15 * 15) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(getSoundName(key), x, y, z, nmsBlock.stepSound.getVolume1(), nmsBlock.stepSound.getVolume2()));
            }
        }
    }

    public String getSoundName(SoundKey key) {
        switch (key) {
            case STEP_SOUND:
                return nmsBlock.stepSound.getStepSound();
            case BREAK_SOUND:
                return nmsBlock.stepSound.getBreakSound();
            case PLACE_SOUND:
                return nmsBlock.stepSound.getPlaceSound();
        }

        return null;
    }

    public enum SoundKey {
        STEP_SOUND,
        BREAK_SOUND,
        PLACE_SOUND
    }
}

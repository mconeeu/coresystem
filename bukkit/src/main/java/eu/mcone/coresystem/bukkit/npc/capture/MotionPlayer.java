/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationProgressEvent;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MotionPlayer extends eu.mcone.coresystem.api.bukkit.npc.capture.Player {
    @Getter
    public MotionCapture capture;

    private final PlayerNpc playerNpc;

    public MotionPlayer(final PlayerNpc playerNpc, final MotionCapture capture) {
        this.playerNpc = playerNpc;
        this.capture = capture;
    }

    @Override
    public void play() {
        currentTick = new AtomicInteger(0);
        AtomicInteger packetsCount = new AtomicInteger(0);
        AtomicInteger currentProgress = new AtomicInteger(0);
        Map<Integer, List<Codec<?, ?>>> codecs = capture.getMotionChunk().getChunkData().getCodecs();

        Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(playerNpc, NpcAnimationStateChangeEvent.NpcAnimationState.START));
        playingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            if (playing) {
                if (Bukkit.getOnlinePlayers().size() != 0) {
                    int tick = currentTick.get();

                    if (packetsCount.get() < codecs.size() - 1) {
                        if (codecs.containsKey(tick)) {
                            for (Codec codec : codecs.get(tick)) {
                                if (CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getCodecRegistry().getEncoderClass(codec.getEncoderID()).equals(PlayerNpc.class)) {
                                    codec.encode(playerNpc);
                                }
                            }

                            int progress = (int) Math.round((100.00 / codecs.size()) * packetsCount.get());
                            if (progress != currentProgress.get()) {
                                currentProgress.set(progress);
                                Bukkit.getPluginManager().callEvent(new NpcAnimationProgressEvent(playerNpc, this.currentTick.get(), progress));
                            }

                            packetsCount.getAndIncrement();
                        }

                        if (forward) {
                            this.currentTick.getAndIncrement();
                        } else if (backward) {
                            this.currentTick.getAndDecrement();
                        }
                    } else {
                        playing = false;
                        playingTask.cancel();
                        Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(playerNpc, NpcAnimationStateChangeEvent.NpcAnimationState.END));
                    }
                }
            }
        }, 1L, 1L);
    }

    private int getPressDuration(final Location blockLocation) {
        if (blockLocation.getBlock().getType() == Material.STONE_BUTTON)
            return 20;
        else if (blockLocation.getBlock().getType() == Material.WOOD_BUTTON)
            return 30;

        return 0;
    }
}

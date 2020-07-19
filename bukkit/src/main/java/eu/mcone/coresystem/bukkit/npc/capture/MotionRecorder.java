/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.Recorder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public class MotionRecorder extends Recorder implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder {

    @Getter
    private final Player player;
    @Getter
    private final String name;
    @Getter
    private final MotionChunk chunk;
    @Getter
    protected long recorded;
    private BukkitTask task;

    public MotionRecorder(final Player player, final String name) {
        super(player.getName(), player.getLocation().getWorld().getName());
        this.player = player;
        this.name = name;
        this.chunk = new MotionChunk();
        this.savedPackets = new AtomicInteger();
    }


    @Override
    public void record() {
        CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getCodecRegistry().listeningForCodecs(true);
        recorded = System.currentTimeMillis() / 1000;

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.getInstance().createActionBar().message("§2§lAufnahme §8│ §a§l" + savedPackets.get() + " §2packet(s)").send(player);
            ticks++;
        }, 1L, 1L);

        CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getCodecRegistry().registerCodecListener((codec, objects) -> {
            if (!isStop()) {
                if (codec.getCodecClass().equals(PlayerMoveEvent.class)) {
                    if (ticks % 2 == 0) {
                        chunk.addPacket(ticks, codec);
                        savedPackets.getAndIncrement();
                    }
                } else {
                    chunk.addPacket(ticks, codec);
                    savedPackets.getAndIncrement();
                }
            }
        });
    }

    @Override
    public void stop() {
        task.cancel();
        CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler().getCodecRegistry().listeningForCodecs(false);
    }
}

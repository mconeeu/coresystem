package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationProgressEvent;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.*;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class MotionPlayer extends eu.mcone.coresystem.api.bukkit.npc.capture.MotionPlayer {

    @Getter
    public MotionCaptureData data;
    @Getter
    private boolean playing;
    public MotionPlayer(final PlayerNpc playerNpc, final MotionCaptureData data) {
        super(playerNpc, data);
    }

    public void playMotionCapture() {
        currentTick = new AtomicInteger(0);
        AtomicInteger packetsCount = new AtomicInteger(0);
        AtomicInteger currentProgress = new AtomicInteger(0);

        Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(playerNpc, NpcAnimationStateChangeEvent.NpcAnimationState.START));
        playingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            if (playing) {
                if (packetsCount.get() < data.getMotionData().size() - 1) {
                    if (data.getMotionData().containsKey(currentTick.get())) {
                        for (PacketWrapper wrapper : data.getMotionData().get(currentTick.get())) {
                            if (wrapper instanceof EntityMovePacketWrapper) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    playerNpc.teleport((((EntityMovePacketWrapper) wrapper).getAsCoreLocation()), player);
                                }
                            }

                            if (wrapper instanceof EntitySneakPacketWrapper) {
                                EntitySneakPacketWrapper sneakPacket = (EntitySneakPacketWrapper) wrapper;
                                if (sneakPacket.getEntityAction().equals(EntityAction.START_SNEAKING)) {
                                    playerNpc.sneak(true);
                                } else {
                                    playerNpc.sneak(false);
                                }
                            }

                            if (wrapper instanceof EntitySwitchItemPacketWrapper) {
                                playerNpc.setItemInHand(((EntitySwitchItemPacketWrapper) wrapper).getItemStack());
                            }

                            if (wrapper instanceof EntityClickPacketWrapper) {
                                playerNpc.sendAnimation(NpcAnimation.SWING_ARM);
                            }

                            if (wrapper instanceof EntityDamagePacketWrapper) {
                                playerNpc.sendAnimation(NpcAnimation.TAKE_DAMAGE);
                            }
                        }

                        int progress = (int) Math.round((100.00 / data.getMotionData().size()) * packetsCount.get());
                        if (progress != currentProgress.get()) {
                            currentProgress.set(progress);
                            Bukkit.getPluginManager().callEvent(new NpcAnimationProgressEvent(playerNpc, currentTick.get(), progress));
                        }

                        packetsCount.getAndIncrement();
                    }

                    if (forward) {
                        currentTick.getAndIncrement();
                    } else if (backward) {
                        currentTick.getAndDecrement();
                    }
                } else {
                    Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(playerNpc, NpcAnimationStateChangeEvent.NpcAnimationState.END));
                    playing = false;
                    playingTask.cancel();
                }
            }
        }, 1L, 1L);
    }
}

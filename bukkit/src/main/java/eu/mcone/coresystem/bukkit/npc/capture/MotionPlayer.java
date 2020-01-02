package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationProgressEvent;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.capture.SimplePlayer;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.*;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import lombok.Getter;
import eu.mcone.coresystem.bukkit.npc.entity.PlayerCoreNpc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class MotionPlayer extends SimplePlayer implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionPlayer {
    @Getter
    public MotionCaptureData data;

    protected PlayerNpc playerNpc;

    public MotionPlayer(final PlayerNpc playerNpc, final MotionCaptureData data) {
        this.playerNpc = playerNpc;
        this.data = data;
    }

    @Override
    public void play() {
        currentTick = new AtomicInteger(0);
        AtomicInteger packetsCount = new AtomicInteger(0);
        AtomicInteger currentProgress = new AtomicInteger(0);

        Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(playerNpc, NpcAnimationStateChangeEvent.NpcAnimationState.START));
        playingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            if (playing) {
                String tick = String.valueOf(currentTick.get());

                if (packetsCount.get() < data.getMotionData().size() - 1) {
                    if (data.getMotionData().containsKey(tick)) {
                        for (PacketWrapper wrapper : data.getMotionData().get(tick)) {
                            if (wrapper instanceof EntityMovePacketWrapper) {
                                ((PlayerCoreNpc) playerNpc).setLocation(((EntityMovePacketWrapper) wrapper).calculateLocation());
                                playerNpc.teleport(((EntityMovePacketWrapper) wrapper).calculateLocation());
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
                                playerNpc.setEquipment(EquipmentPosition.HAND, ((EntitySwitchItemPacketWrapper) wrapper).constructItemStack());
                            }

                            if (wrapper instanceof EntityClickPacketWrapper) {
                                playerNpc.sendAnimation(NpcAnimation.SWING_ARM);
                            }

                            if (wrapper instanceof EntityButtonInteractPacketWrapper) {
                                BlockStateDirection FACING = BlockStateDirection.of("facing");
                                EntityButtonInteractPacketWrapper packet = (EntityButtonInteractPacketWrapper) wrapper;
                                Location location = packet.calculateLocation();
                                WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
                                BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
                                IBlockData iblockdata = world.getType(blockposition);
                                Block block = iblockdata.getBlock();

                                block.interact(((CraftWorld) location.getWorld()).getHandle(), blockposition, iblockdata, null, iblockdata.get(FACING), (float) location.getX(), (float) location.getY(), (float) location.getZ());
                            } else if (wrapper instanceof EntityOpenDoorPacketWrapper) {
                                EntityOpenDoorPacketWrapper packet = (EntityOpenDoorPacketWrapper) wrapper;
                                Location location = packet.calculateLocation();
                                BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
                                WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
                                IBlockData iblockdata = world.getType(blockposition);

                                BlockStateBoolean OPEN = BlockStateBoolean.of("open");
                                BlockStateEnum<BlockDoor.EnumDoorHalf> HALF = BlockStateEnum.of("half", BlockDoor.EnumDoorHalf.class);

                                BlockPosition blockposition1 = iblockdata.get(HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
                                IBlockData iblockdata1 = blockposition.equals(blockposition1) ? iblockdata : world.getType(blockposition1);

                                iblockdata = iblockdata1.a(OPEN);
                                world.setTypeAndData(blockposition1, iblockdata, 2);
                                world.b(blockposition1, blockposition);
                                world.a(iblockdata.get(OPEN) ? 1003 : 1006, blockposition, 0);

                                if (packet.isDoorOpen()) {
                                    location.getWorld().playSound(location, Sound.DOOR_OPEN, 1, 1);
                                } else {
                                    location.getWorld().playSound(location, Sound.DOOR_CLOSE, 1, 1);
                                }
                            }

                            if (wrapper instanceof EntityDamagePacketWrapper) {
                                playerNpc.sendAnimation(NpcAnimation.TAKE_DAMAGE);
                            }
                        }

                        int progress = (int) Math.round((100.00 / data.getMotionData().size()) * packetsCount.get());
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

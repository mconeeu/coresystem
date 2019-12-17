package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.*;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Door;

import java.util.List;
import java.util.Map;

public class MotionRecorder extends eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder {

    public MotionRecorder(final Player player, final String name) {
        super(player, name);
    }

    public void record() {
        recorded = System.currentTimeMillis() / 1000;

        taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.getInstance().createActionBar().message("§2§lAufnahme §8│ §a§l" + savedPackets.get() + " §2packet(s)").send(player);
            ticks++;
        }, 1L, 1L);

        CoreSystem.getInstance().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(PlayerMoveEvent e) {
                if (player.equals(e.getPlayer())) {
                    addData(new EntityMovePacketWrapper(e.getPlayer().getLocation()));

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(PlayerItemHeldEvent e) {
                if (player.equals(e.getPlayer())) {
                    addData(new EntitySwitchItemPacketWrapper(e.getPlayer().getItemInHand()));

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(PlayerInteractEvent e) {
                if (player.equals(e.getPlayer())) {
                    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        if (e.getClickedBlock().getType().equals(Material.STONE_BUTTON) || e.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
                            BlockState blockState = e.getClickedBlock().getState();
                            addData(new EntityButtonInteractPacketWrapper(e.getClickedBlock().getLocation(), ((Button) blockState.getData()).isPowered()));
                            addData(new EntityClickPacketWrapper());
                        } else if (e.getClickedBlock().getType().toString().contains("DOOR")) {
                            BlockState blockState = e.getClickedBlock().getState();
                            addData(new EntityOpenDoorPacketWrapper(e.getClickedBlock().getLocation(), ((Door) blockState.getData()).isOpen()));
                            addData(new EntityClickPacketWrapper());
                        }
                    } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        addData(new EntityClickPacketWrapper());
                    }

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(EntityDamageEvent e) {
                if (e.getEntity() instanceof Player) {
                    if (player.equals(e.getEntity())) {
                        addData(new EntityDamagePacketWrapper());

                        if (isStopped()) {
                            e.getHandlers().unregister(this);
                        }
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(PlayerToggleSneakEvent e) {
                if (player.equals(e.getPlayer())) {
                    if (e.isSneaking()) {
                        addData(new EntitySneakPacketWrapper(EntityAction.START_SNEAKING));
                    } else {
                        addData(new EntitySneakPacketWrapper(EntityAction.STOP_SNEAKING));
                    }

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }
        });
    }

    public Map<String, List<PacketWrapper>> stopRecording() {
        isStopped = true;
        taskID.cancel();
        return packets;
    }
}

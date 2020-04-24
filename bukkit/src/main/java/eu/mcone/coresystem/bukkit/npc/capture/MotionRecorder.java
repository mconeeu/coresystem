package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.SimpleRecorder;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.*;
import org.bukkit.Bukkit;
import lombok.Getter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MotionRecorder extends SimpleRecorder implements Listener, eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder {

    @Getter
    private final String recorderName;
    @Getter
    private final String world;
    @Getter
    private final String name;
    @Getter
    protected long recorded;

    @Getter
    public HashMap<String, List<PacketContainer>> packets;

    private final Player player;

    public MotionRecorder(final Player player, final String name) {
        this.player = player;
        this.name = name;
        this.recorderName = player.getName();
        this.world = player.getLocation().getWorld().getName();
        this.savedPackets = new AtomicInteger();
        packets = new HashMap<>();
    }

    @Override
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
                    addData(new EntityMovePacketContainer(e.getPlayer().getLocation()));

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void on(PlayerItemHeldEvent e) {
                if (player.equals(e.getPlayer())) {
                    addData(new EntitySwitchItemPacketContainer(e.getPlayer().getItemInHand()));

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
                            addData(new EntityButtonInteractPacketContainer(e.getClickedBlock().getLocation(), ((Button) blockState.getData()).isPowered()));
                            addData(new EntityClickPacketContainer());
                        } else if (e.getClickedBlock().getType().toString().contains("DOOR")) {
                            BlockState blockState = e.getClickedBlock().getState();
                            addData(new EntityOpenDoorPacketContainer(e.getClickedBlock().getLocation(), ((Door) blockState.getData()).isOpen()));
                            addData(new EntityClickPacketContainer());
                        }
                    } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        addData(new EntityClickPacketContainer());
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
                        addData(new EntityDamagePacketContainer());

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
                        addData(new EntitySneakPacketContainer(EntityAction.START_SNEAKING));
                    } else {
                        addData(new EntitySneakPacketContainer(EntityAction.STOP_SNEAKING));
                    }

                    if (isStopped()) {
                        e.getHandlers().unregister(this);
                    }
                }
            }
        });
    }

    protected void addData(PacketContainer data) {
        String tick = String.valueOf(ticks);
        if (this.packets.containsKey(tick)) {
            this.packets.get(tick).add(data);
        } else {
            this.packets.put(tick, new ArrayList<PacketContainer>() {{
                add(data);
            }});
        }

        savedPackets.getAndIncrement();
    }

    public Map<String, List<PacketContainer>> stopRecording() {
        isStopped = true;
        taskID.cancel();
        return packets;
    }
}

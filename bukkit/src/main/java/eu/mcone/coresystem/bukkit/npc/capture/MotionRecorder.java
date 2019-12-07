package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;
import java.util.Map;

public class MotionRecorder extends eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder {

    public MotionRecorder(final Player player) {
        super(player);
    }

    public void record() {
        CoreSystem.getInstance().registerEvents(new Listener() {
            @EventHandler
            public void on(PlayerMoveEvent e) {
                addData(new EntityMovePacketWrapper(e.getPlayer().getLocation()));

                if (isStopped()) {
                    e.getHandlers().unregister(this);
                }
            }

            @EventHandler
            public void on(PlayerItemHeldEvent e) {
                addData(new EntitySwitchItemPacketWrapper(e.getPlayer().getItemInHand()));

                if (isStopped()) {
                    e.getHandlers().unregister(this);
                }
            }

            @EventHandler
            public void on(PlayerInteractEvent e) {
                if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    addData(new EntityClickPacketWrapper());
                }

                if (isStopped()) {
                    e.getHandlers().unregister(this);
                }
            }

            @EventHandler
            public void on(EntityDamageEvent e) {
                addData(new EntityDamagePacketWrapper());

                if (isStopped()) {
                    e.getHandlers().unregister(this);
                }
            }

            @EventHandler
            public void on(PlayerToggleSneakEvent e) {
                if (e.isSneaking()) {
                    addData(new EntitySneakPacketWrapper(EntityAction.START_SNEAKING));
                } else {
                    addData(new EntitySneakPacketWrapper(EntityAction.STOP_SNEAKING));
                }

                if (isStopped()) {
                    e.getHandlers().unregister(this);
                }
            }
        });
    }

    @Override
    public Map<Integer, List<PacketWrapper>> stopRecording() {
        taskID.cancel();
        return packets;
    }
}

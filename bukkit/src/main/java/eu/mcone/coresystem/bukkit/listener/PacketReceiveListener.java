package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.nms.PacketReceiveEvent;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketReceiveListener implements Listener {

    @EventHandler
    public void on(PacketReceiveEvent event) {
        if (event.getObject() instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) event.getObject();

            if (packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) || packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)) {
                try {
                    NPC npc = BukkitCoreSystem.getSystem().getNpcManager().getNPC((int) ReflectionManager.getValue(packet, "a"));

                    if (npc != null) {
                        Bukkit.getScheduler().runTask(
                                BukkitCoreSystem.getSystem(),
                                () -> Bukkit.getPluginManager().callEvent(new NpcInteractEvent(event.getPlayer(), npc, packet.a()))
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package eu.mcone.coresystem.api.bukkit.event.nms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
@AllArgsConstructor
public class PacketReceiveEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Packet<?> object;
    private final List<Object> list;
    private final Player player;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

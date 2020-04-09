package eu.mcone.coresystem.api.bukkit.event.nms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class PacketSendEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Packet packet;
    private final EntityPlayer player;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

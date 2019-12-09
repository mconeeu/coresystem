package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.Getter;

@Getter
public class EntityButtonInteractPacketWrapper extends PacketWrapper {

    private final CoreLocation blockLocation;
    private final boolean isPressed;

    public EntityButtonInteractPacketWrapper(final CoreLocation blockLocation, final boolean isPressed) {
        super(PacketType.ENTITY_ACTION);
        this.blockLocation = blockLocation;
        this.isPressed = isPressed;
    }
}

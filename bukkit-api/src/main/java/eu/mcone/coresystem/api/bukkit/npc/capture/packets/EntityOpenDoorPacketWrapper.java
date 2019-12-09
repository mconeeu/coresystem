package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.Getter;

public class EntityOpenDoorPacketWrapper extends PacketWrapper {

    @Getter
    private final CoreLocation blockLocation;
    private final boolean openDoor;

    public EntityOpenDoorPacketWrapper(final CoreLocation blockLocation, final boolean openDoor) {
        super(PacketType.ENTITY_ACTION);
        this.blockLocation = blockLocation;
        this.openDoor = openDoor;
    }

    public boolean openDoor() {
        return openDoor;
    }
}

package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;

import java.io.Serializable;

public abstract class PacketWrapper implements Serializable  {

    private static final long serialVersionUID = 191935L;

    @Getter
    private final PacketType packetType;
    @Getter
    private EntityAction entityAction;

    public PacketWrapper(final PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketWrapper(final EntityAction entityAction) {
        this.packetType = PacketType.ENTITY_ACTION;
        this.entityAction = entityAction;
    }
}

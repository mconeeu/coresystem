package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;

@BsonDiscriminator
@Getter
public abstract class PacketContainer implements Serializable {

    private PacketTyp packetType;
    private EntityAction entityAction;

    @BsonCreator
    public PacketContainer(@BsonProperty("packetType") final PacketTyp packetType, @BsonProperty("entityAction") final EntityAction entityAction) {
        this.packetType = packetType;
        this.entityAction = entityAction;
    }

    public PacketContainer(final PacketTyp packetType) {
        this.packetType = packetType;
    }

    public PacketContainer(final EntityAction entityAction) {
        this.packetType = PacketTyp.ENTITY;
        this.entityAction = entityAction;
    }
}

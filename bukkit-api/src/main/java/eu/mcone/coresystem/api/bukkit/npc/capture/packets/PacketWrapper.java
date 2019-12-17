package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;

@BsonDiscriminator
@Getter
public abstract class PacketWrapper {

    private PacketType packetType;
    private EntityAction entityAction;

    @BsonCreator
    public PacketWrapper(@BsonProperty("packetType") final PacketType packetType, @BsonProperty("entityAction") final EntityAction entityAction) {
        this.packetType = packetType;
        this.entityAction = entityAction;
    }

    public PacketWrapper(final PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketWrapper(final EntityAction entityAction) {
        this.packetType = PacketType.ENTITY_ACTION;
        this.entityAction = entityAction;
    }
}

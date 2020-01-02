package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Server;
import org.bukkit.World;

import java.io.Serializable;

@BsonDiscriminator
@Getter
public abstract class PacketWrapper {

    private PacketType packetType;
    private EntityAction entityAction;
    private WorldAction worldAction;
    private ServerAction serverAction;

    @BsonCreator
    public PacketWrapper(@BsonProperty("packetType") final PacketType packetType, @BsonProperty("entityAction") final EntityAction entityAction,
                         @BsonProperty("worldAction") final WorldAction worldAction, @BsonProperty("serverAction") final ServerAction serverAction) {
        this.packetType = packetType;
        this.entityAction = entityAction;
        this.worldAction = worldAction;
        this.serverAction = serverAction;
    }

    public PacketWrapper(final PacketType packetType, final EntityAction entityAction) {
        this.packetType = packetType;
        this.entityAction = entityAction;
    }

    public PacketWrapper(final PacketType packetType, final WorldAction worldAction) {
        this.packetType = packetType;
        this.worldAction = worldAction;
    }

    public PacketWrapper(final PacketType packetType, final ServerAction serverAction) {
        this.packetType = packetType;
        this.serverAction = serverAction;
    }

    public PacketWrapper(final PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketWrapper(final EntityAction entityAction) {
        this.packetType = PacketType.ENTITY;
        this.entityAction = entityAction;
    }
}

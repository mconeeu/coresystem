package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class EntitySneakPacketWrapper extends PacketWrapper {

    public EntitySneakPacketWrapper() {
        super(PacketType.ENTITY, EntityAction.START_SNEAKING);
    }

    public EntitySneakPacketWrapper(final EntityAction entityAction) {
        super(PacketType.ENTITY, entityAction);
    }

    @BsonCreator
    public EntitySneakPacketWrapper(@BsonProperty("packetType") final PacketType packetType, @BsonProperty("entityAction") final EntityAction entityAction) {
        super(packetType, entityAction);
    }
}

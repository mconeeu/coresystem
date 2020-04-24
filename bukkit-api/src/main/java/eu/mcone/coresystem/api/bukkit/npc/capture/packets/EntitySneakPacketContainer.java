package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class EntitySneakPacketContainer extends PacketContainer {

    public EntitySneakPacketContainer() {
        super(PacketTyp.ENTITY, EntityAction.START_SNEAKING);
    }

    public EntitySneakPacketContainer(final EntityAction entityAction) {
        super(PacketTyp.ENTITY, entityAction);
    }

    @BsonCreator
    public EntitySneakPacketContainer(@BsonProperty("packetType") final PacketTyp packetType, @BsonProperty("entityAction") final EntityAction entityAction) {
        super(packetType, entityAction);
    }
}

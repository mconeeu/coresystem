package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class EntityClickPacketContainer extends PacketContainer {

    public EntityClickPacketContainer() {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);
    }

    @BsonCreator
    public EntityClickPacketContainer(@BsonProperty("entityAction") final EntityAction entityAction) {
        super(PacketTyp.ENTITY, entityAction);
    }
}

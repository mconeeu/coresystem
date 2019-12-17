package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class EntityClickPacketWrapper extends PacketWrapper {

    public EntityClickPacketWrapper() {
        super(EntityAction.CLICK);
    }

    @BsonCreator
    public EntityClickPacketWrapper(@BsonProperty("entityAction") final EntityAction entityAction) {
        super(entityAction);
    }
}

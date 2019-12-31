package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class EntityDamagePacketWrapper extends PacketWrapper {

    public EntityDamagePacketWrapper() {
        super(PacketType.ENTITY_DAMAGE);
    }

    @BsonCreator
    public EntityDamagePacketWrapper(@BsonProperty("packetType") final PacketType packetType) {
        super(packetType);
    }
}

package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.templates.EntityLocationWrapperTemplate;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityMovePacketWrapper extends EntityLocationWrapperTemplate {

    public EntityMovePacketWrapper(final Location location) {
        super(PacketType.ENTITY, EntityAction.MOVE, location);
    }

    public EntityMovePacketWrapper(double x, double y, double z, float yaw, float pitch, String world) {
        super(PacketType.ENTITY, EntityAction.MOVE, x, y, z, yaw, pitch, world);
    }
}

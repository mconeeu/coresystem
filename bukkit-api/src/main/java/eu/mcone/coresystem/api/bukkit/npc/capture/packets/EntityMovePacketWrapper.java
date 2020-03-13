package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.templates.EntityLocationWrapperTemplate;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityMovePacketWrapper extends EntityLocationWrapperTemplate {

    public EntityMovePacketWrapper(final Location location) {
        super(PacketType.ENTITY, EntityAction.MOVE, location);
    }
}

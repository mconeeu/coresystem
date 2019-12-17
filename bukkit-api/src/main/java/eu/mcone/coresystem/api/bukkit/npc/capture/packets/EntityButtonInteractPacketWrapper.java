package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityButtonInteractPacketWrapper extends PacketWrapper {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;
    private boolean pressed;

    public EntityButtonInteractPacketWrapper() {
        super(PacketType.ENTITY_ACTION);
    }

    public EntityButtonInteractPacketWrapper(final Location location, final boolean pressed) {
        super(PacketType.ENTITY_ACTION);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
        this.pressed = pressed;
    }

    @BsonCreator
    public EntityButtonInteractPacketWrapper(@BsonProperty("x") final double x, @BsonProperty("y") final double y, @BsonProperty("z") final double z, @BsonProperty("yaw") final float yaw,
                                       @BsonProperty("pitch") final float pitch, @BsonProperty("worldName") final String worldName, @BsonProperty("pressed") final boolean pressed) {
        super(PacketType.ENTITY_ACTION);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
        this.pressed = pressed;
    }

    @BsonIgnore
    public CoreLocation getAsCoreLocation() {
        return new CoreLocation(worldName, x, y, z, yaw, pitch);
    }
}

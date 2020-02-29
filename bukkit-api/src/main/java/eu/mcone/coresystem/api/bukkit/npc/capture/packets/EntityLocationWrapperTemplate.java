package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityLocationWrapperTemplate extends PacketWrapper {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;

    public EntityLocationWrapperTemplate(final PacketType typ, final EntityAction action, final Location location) {
        super(typ, action);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
    }

    @BsonCreator
    public EntityLocationWrapperTemplate(@BsonProperty("packetType") final PacketType packetType, @BsonProperty("entityAction") final EntityAction entityAction,
                                         @BsonProperty("x") final double x, @BsonProperty("y") final double y, @BsonProperty("z") final double z,
                                         @BsonProperty("yaw") final float yaw, @BsonProperty("pitch") final float pitch, @BsonProperty("worldName") final String worldName) {
        super(packetType, entityAction);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }

    public Location calculateLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}

package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityButtonInteractPacketContainer extends PacketContainer {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;
    private boolean pressed;

    public EntityButtonInteractPacketContainer() {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);
    }

    public EntityButtonInteractPacketContainer(final Location location, final boolean pressed) {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
        this.pressed = pressed;
    }

    @BsonCreator
    public EntityButtonInteractPacketContainer(@BsonProperty("x") final double x, @BsonProperty("y") final double y, @BsonProperty("z") final double z, @BsonProperty("yaw") final float yaw,
                                               @BsonProperty("pitch") final float pitch, @BsonProperty("worldName") final String worldName, @BsonProperty("pressed") final boolean pressed) {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
        this.pressed = pressed;
    }

    public Location calculateLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

}

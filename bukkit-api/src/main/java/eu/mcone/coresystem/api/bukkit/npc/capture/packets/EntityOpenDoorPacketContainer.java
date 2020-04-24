package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public class EntityOpenDoorPacketContainer extends PacketContainer {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;
    private boolean doorOpen;

    public EntityOpenDoorPacketContainer() {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);
    }

    public EntityOpenDoorPacketContainer(final Location location, final boolean doorOpen) {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
        this.doorOpen = doorOpen;
    }

    @BsonCreator
    public EntityOpenDoorPacketContainer(@BsonProperty("x") final double x, @BsonProperty("y") final double y, @BsonProperty("z") final double z, @BsonProperty("yaw") final float yaw,
                                         @BsonProperty("pitch") final float pitch, @BsonProperty("worldName") final String worldName, @BsonProperty("doorOpen") final boolean doorOpen) {
        super(PacketTyp.ENTITY, EntityAction.INTERACT);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
        this.doorOpen = doorOpen;
    }

    public Location calculateLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}

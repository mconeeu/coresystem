package eu.mcone.coresystem.api.bukkit.npc.capture.packets.templates;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.EntityAction;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketTyp;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketContainer;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@BsonDiscriminator
@Getter
public abstract class EntityLocationTContainer extends PacketContainer {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;

    public EntityLocationTContainer(final PacketTyp typ, final EntityAction action, final Location location) {
        super(typ, action);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
    }

    @BsonCreator
    public EntityLocationTContainer(@BsonProperty("packetType") final PacketTyp packetType, @BsonProperty("entityAction") final EntityAction entityAction,
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

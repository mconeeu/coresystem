package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.Getter;
import org.bukkit.Location;

import java.io.Serializable;

public class EntityMovePacketWrapper extends PacketWrapper implements Serializable {

    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;
    @Getter
    private final float yaw;
    @Getter
    private final float pitch;
    @Getter
    private final String worldName;

    public EntityMovePacketWrapper(final Location location) {
        super(PacketType.POSITION);

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();

        this.worldName = location.getWorld().getName();
    }

    public CoreLocation getAsCoreLocation() {
        return new CoreLocation(worldName, x, y, z, yaw, pitch);
    }
}

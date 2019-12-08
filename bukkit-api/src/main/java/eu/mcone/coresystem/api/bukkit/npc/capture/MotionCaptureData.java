package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.Getter;
import org.bson.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
public class MotionCaptureData implements Serializable {

    @Getter
    private String name;
    @Getter
    private String world;
    @Getter
    private long recorded;
    @Getter
    private String creator;
    @Getter
    private int length;
    @Getter
    private Map<Integer, List<PacketWrapper>> motionData;


    public MotionCaptureData(final String name, final MotionRecorder motionRecorder) {
        this.name = name;
        this.world = motionRecorder.getWorld();
        this.recorded = motionRecorder.getRecorded();
        this.creator = motionRecorder.getRecorderName();
        this.length = motionRecorder.getTicks();
        this.motionData = motionRecorder.getPackets();
    }

    public MotionCaptureData(final String name, final String worldName, final long recorded, final String creator, final int length, final Map<Integer, List<PacketWrapper>> motionData) {
        this.name = name;
        this.world = worldName;
        this.recorded = recorded;
        this.creator = creator;
        this.length = length;
        this.motionData = motionData;
    }

    public Document createBsonDocument() {
        return new Document("name", name).append("world", world).append("recorded", recorded).append("creator", creator).append("length", length).append("packets", GenericUtils.serialize(motionData));
    }
}

package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.Map;

@Getter
@BsonDiscriminator
public class MotionCaptureData {

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
    private Map<String, List<PacketWrapper>> motionData;

    public MotionCaptureData() {}

    public MotionCaptureData(final String name, final MotionRecorder motionRecorder) {
        this.name = name;
        this.world = motionRecorder.getWorld();
        this.recorded = motionRecorder.getRecorded();
        this.creator = motionRecorder.getRecorderName();
        this.length = motionRecorder.getTicks();
        this.motionData = motionRecorder.getPackets();
    }

    @BsonCreator
    public MotionCaptureData(@BsonProperty("name") final String name, @BsonProperty("world") final String worldName, @BsonProperty("recorded") final long recorded,
                             @BsonProperty("creator") final String creator, @BsonProperty("length") final int length, @BsonProperty("motionData") final Map<String, List<PacketWrapper>> motionData) {
        this.name = name;
        this.world = worldName;
        this.recorded = recorded;
        this.creator = creator;
        this.length = length;
        this.motionData = motionData;
    }
}

package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.types.Binary;

public class MotionCapture implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionCapture {

    @Getter
    private final String name;
    @Getter
    private final String creator;
    @Getter
    private final long recorded;
    @Getter
    private final String world;
    @Getter
    private final int length;

    @Getter
    private final eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk motionChunk;

    public MotionCapture(MotionRecorder motionRecorder) {
        this.name = motionRecorder.getName();
        this.creator = motionRecorder.getPlayer().getName();
        this.recorded = motionRecorder.getRecorded();
        this.world = motionRecorder.getWorld();
        this.length = motionRecorder.getTicks();
        this.motionChunk = motionRecorder.getChunk();
    }

    public MotionCapture(Document document, CodecRegistry registry) {
        this.name = document.getString("name");
        this.creator = document.getString("creator");
        this.recorded = document.getLong("recorded");
        this.world = document.getString("world");
        this.length = document.getInteger("length");
        this.motionChunk = new MotionChunk(registry, document.get("chunk", Binary.class).getData());
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("creator", creator)
                .append("recorded", recorded)
                .append("world", world)
                .append("length", length)
                .append("chunk", GenericUtils.serialize(motionChunk.getChunkData()));
    }
}

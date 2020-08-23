package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecInputStream;
import eu.mcone.coresystem.api.bukkit.codec.MultipleCodecCallback;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionRecorder;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.types.Binary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk motionChunk;

    public MotionCapture(MotionRecorder motionRecorder) {
        this.name = motionRecorder.getName();
        this.creator = motionRecorder.getPlayer().getName();
        this.recorded = motionRecorder.getRecorded();
        this.world = motionRecorder.getWorld();
        this.length = motionRecorder.getTicks();
        this.motionChunk = motionRecorder.getChunk();
    }

    public MotionCapture(MotionCapture motionCapture, MotionChunk chunk) {
        this.name = motionCapture.getName();
        this.creator = motionCapture.getCreator();
        this.recorded = motionCapture.getRecorded();
        this.world = motionCapture.getWorld();
        this.length = motionCapture.getLength();
        this.motionChunk = chunk;
    }

    public MotionCapture(MotionCaptureHandler motionCaptureHandler, Document document) {
        this.name = document.getString("name");
        this.creator = document.getString("creator");
        this.recorded = document.getLong("recorded");
        this.world = document.getString("world");
        this.length = document.getInteger("length");

        byte[] genericChunkData = document.get("chunk", Binary.class).getData();

        if (genericChunkData != null) {
            CodecInputStream inputStream = new CodecInputStream(motionCaptureHandler.getCodecRegistry());
            boolean migrated = false;
            Map<Integer, byte[]> mapData = GenericUtils.deserialize(HashMap.class, genericChunkData);

            if (mapData != null) {
                Map<Integer, List<Codec<?, ?>>> codecs = new HashMap<>();

                MultipleCodecCallback callback;
                for (Map.Entry<Integer, byte[]> mapDataEntry : mapData.entrySet()) {
                    callback = inputStream.readAsList(mapDataEntry.getValue());
                    codecs.put(mapDataEntry.getKey(), callback.getCodecs());

                    if (callback.getMigrated() > 0 && Boolean.parseBoolean(System.getProperty("SaveCodecMigrations"))) {
                        mapData.put(mapDataEntry.getKey(), callback.getMigratedCodecs());
                        migrated = true;
                    }
                }

                if (migrated) {
                    motionCaptureHandler.migrateChunk(name, GenericUtils.serialize(mapData));
                }

                this.motionChunk = new MotionChunk(new MotionChunk.MotionChunkData(codecs));
            }
        } else {
            throw new NullPointerException("Could not encode byte array to motion chunk data");
        }
    }

    public Document toDocument() {
        return new Document("name", name)
                .append("creator", creator)
                .append("recorded", recorded)
                .append("world", world)
                .append("length", length)
                .append("chunk", motionChunk.getChunkData().serialize());
    }
}

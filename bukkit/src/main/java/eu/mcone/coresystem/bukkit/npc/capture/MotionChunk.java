package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.packets.Chunk;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MotionChunk extends Chunk implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk {

    private final MotionChunkData chunkData;

    public MotionChunk(CodecRegistry codecRegistry) {
        super(codecRegistry);
        chunkData = new MotionChunkData(codecRegistry);
    }

    public MotionChunk(CodecRegistry codecRegistry, byte[] genericData) {
        super(codecRegistry);
        this.chunkData = GenericUtils.deserialize(MotionChunkData.class, genericData);
    }

    public void addPacket(int tick, Codec<?> codec) {
        if (chunkData.codecs.containsKey(tick)) {
            chunkData.codecs.get(tick).add(codec);
        } else {
            chunkData.codecs.put(tick, new ArrayList<Codec<?>>() {{
                add(codec);
            }});
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class MotionChunkData extends eu.mcone.coresystem.api.bukkit.packets.ChunkData implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk.MotionChunkData {
        @Getter
        private Map<Integer, List<Codec<?>>> codecs;

        public MotionChunkData(CodecRegistry codecRegistry) {
            super(codecRegistry);
            codecs = new HashMap<>();
        }
    }
}

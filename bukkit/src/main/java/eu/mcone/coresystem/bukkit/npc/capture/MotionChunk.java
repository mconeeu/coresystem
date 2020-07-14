package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.packets.Chunk;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MotionChunk extends Chunk implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk {

    private final MotionChunkData chunkData;

    public MotionChunk() {
        chunkData = new MotionChunkData();
    }

    public MotionChunk(MotionChunkData chunkData) {
        this.chunkData = chunkData;
    }

    public void addPacket(int tick, Codec<?, ?> codec) {
        if (chunkData.codecs.containsKey(tick)) {
            chunkData.codecs.get(tick).add(codec);
        } else {
            chunkData.codecs.put(tick, new ArrayList<Codec<?, ?>>() {{
                add(codec);
            }});
        }
    }

    @AllArgsConstructor
    public static class MotionChunkData extends eu.mcone.coresystem.api.bukkit.packets.ChunkData implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk.MotionChunkData {
        @Getter
        private final Map<Integer, List<Codec<?, ?>>> codecs;

        public MotionChunkData() {
            codecs = new HashMap<>();
        }

        @Override
        public int getLength() {
            return codecs.size();
        }
    }
}

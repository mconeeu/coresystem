package eu.mcone.coresystem.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.binary.CodecOutputStream;
import eu.mcone.coresystem.api.bukkit.codec.binary.CodecSerializedCallback;
import group.onegaming.networkmanager.core.api.util.GenericUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MotionChunk implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk {

    private final MotionChunk.MotionChunkData chunkData;

    public MotionChunk() {
        chunkData = new MotionChunk.MotionChunkData();
    }

    public MotionChunk(MotionChunk.MotionChunkData chunkData) {
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

    public static class MotionChunkData implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk.MotionChunkData {
        @Getter
        private final Map<Integer, List<Codec<?, ?>>> codecs;

        public MotionChunkData(Map<Integer, List<Codec<?, ?>>> codecs) {
            this.codecs = codecs;
        }

        public MotionChunkData() {
            codecs = new HashMap<>();
        }

        public byte[] serialize() {
            Map<Integer, byte[]> generic = new HashMap<>();
            CodecOutputStream stream = new CodecOutputStream();

            for (Map.Entry<Integer, List<Codec<?, ?>>> entry : codecs.entrySet()) {
                stream.write(entry.getValue(), new CodecSerializedCallback() {
                    @Override
                    public void finished(boolean migrated, byte[] binary, Codec<?, ?>... codecs) {
                        generic.put(entry.getKey(), binary);
                    }

                    @Override
                    public void error() {
                    }
                });
            }

            return GenericUtils.serialize(generic);
        }

        public int getLength() {
            return codecs.size();
        }
    }
}

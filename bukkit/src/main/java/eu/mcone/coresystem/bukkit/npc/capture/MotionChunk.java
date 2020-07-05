package eu.mcone.coresystem.bukkit.npc.capture;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.packets.Chunk;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.core.util.CompressUtils;
import eu.mcone.coresystem.api.core.util.GenericUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
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
        this.chunkData = new MotionChunkData(codecRegistry, genericData);
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
    public static class MotionChunkData extends eu.mcone.coresystem.api.bukkit.packets.ChunkData implements eu.mcone.coresystem.api.bukkit.npc.capture.MotionChunk.MotionChunkData, Serializable {
        @Getter
        private transient Map<Integer, List<Codec<?>>> codecs;
        private byte[] genericData;


        public MotionChunkData(CodecRegistry codecRegistry) {
            super(codecRegistry);
            codecs = new HashMap<>();
        }

        public MotionChunkData(CodecRegistry codecRegistry, byte[] genericData) {
            super(codecRegistry);
            codecs = new HashMap<>();
            this.genericData = genericData;
            deserialize();
        }

        public byte[] serialize() {
            try {
                ByteArrayDataOutput out;
                Map<Integer, List<byte[]>> generic = new HashMap<>();

                for (Map.Entry<Integer, List<Codec<?>>> entry : codecs.entrySet()) {
                    for (Codec<?> codec : entry.getValue()) {
                        out = ByteStreams.newDataOutput();
                        out.writeUTF(codec.getClass().getName());
                        codec.onWrite(out);

                        byte[] copiedArray = out.toByteArray();
                        if (generic.containsKey(entry.getKey())) {
                            generic.get(entry.getKey()).add(copiedArray);
                        } else {
                            generic.put(entry.getKey(), new ArrayList<byte[]>() {{
                                add(copiedArray);
                            }});
                        }
                    }
                }

                genericData = CompressUtils.compress(GenericUtils.serialize(generic));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return genericData;
        }

        public void deserialize() {
            byte[] unCompressed = CompressUtils.unCompress(genericData);

            if (unCompressed != null) {
                Map<Integer, List<byte[]>> generic = (Map<Integer, List<byte[]>>) GenericUtils.deserialize(Map.class, unCompressed);

                if (generic != null) {
                    try {
                        DataInputStream inputStream;

                        for (Map.Entry<Integer, List<byte[]>> genericEntry : generic.entrySet()) {
                            for (byte[] array : genericEntry.getValue()) {
                                inputStream = new DataInputStream(new ByteArrayInputStream(array));

                                if (inputStream.available() > 0) {
                                    String className = inputStream.readUTF();
                                    Class<? extends Codec<?>> codecClass = getCodecRegistry().getCodecForClassName(className);

                                    if (codecClass != null) {
                                        Codec<?> codec = codecClass.newInstance();

                                        if (codecs.containsKey(genericEntry.getKey())) {
                                            codecs.get(genericEntry.getKey()).add(codec);
                                        } else {
                                            codecs.put(genericEntry.getKey(), new ArrayList<Codec<?>>() {{
                                                add(codec);
                                            }});
                                        }
                                    } else {
                                        CoreSystem.getInstance().sendConsoleMessage("§cCodec " + className + " not registered!");
                                    }
                                }
                            }
                        }

                        genericData = GenericUtils.serialize(generic);
                    } catch (IOException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

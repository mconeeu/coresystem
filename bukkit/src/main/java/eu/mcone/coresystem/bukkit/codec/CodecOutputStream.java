package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.codec.Codec;

import java.io.*;
import java.util.List;

import static eu.mcone.coresystem.bukkit.codec.CodecRegistry.getCodecVersion;

public class CodecOutputStream {

    public CodecOutputStream() {
    }

    public byte[] serialize(List<Codec<?, ?>> codecs) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            for (Codec<?, ?> codec : codecs) {
                byte version = getCodecVersion(codec.getClass());
                System.out.println(codec.toString());

                if (version != 0) {
                    dataOutputStream.writeByte(codec.getCodecID());
                    dataOutputStream.writeByte(version);
                    codec.writeObject(dataOutputStream);
                } else {
                    throw new IllegalArgumentException("Could not get version from codec " + codec.getClass().getSimpleName() + " (version > 0?)");
                }
            }

            byte[] length = byteArrayOutputStream.toByteArray();
            System.out.println("Serialized length: " + length.length);
            return length;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

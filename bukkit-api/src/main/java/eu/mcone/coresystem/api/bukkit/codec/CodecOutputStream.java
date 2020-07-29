package eu.mcone.coresystem.api.bukkit.codec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


public class CodecOutputStream implements Serializable {

    public CodecOutputStream() {
    }

    public byte[] serialize(List<Codec<?, ?>> codecs) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            for (Codec<?, ?> codec : codecs) {
                byte version = CodecRegistry.getCodecVersion(codec.getClass());

                if (version != 0) {
                    dataOutputStream.writeByte(codec.getCodecID());
                    dataOutputStream.writeByte(version);
                    codec.writeObject(dataOutputStream);
                } else {
                    throw new IllegalArgumentException("Could not get version from codec " + codec.getClass().getSimpleName() + " (version > 0?)");
                }
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

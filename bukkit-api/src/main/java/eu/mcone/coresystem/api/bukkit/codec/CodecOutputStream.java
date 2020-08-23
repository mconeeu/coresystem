package eu.mcone.coresystem.api.bukkit.codec;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


public class CodecOutputStream implements Serializable {

    public CodecOutputStream() {
    }

    public byte[] write(Codec<?, ?> codec) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        write(codec, dataOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] write(List<Codec<?, ?>> codecs) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        write(codecs, dataOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void write(List<Codec<?, ?>> codecs, DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(codecs.size());

            for (Codec<?, ?> codec : codecs) {
                write(codec, dataOutputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Codec<?, ?> codec, DataOutputStream dataOutputStream) {
        try {
            System.out.println("----------------------------");
            System.out.println("CLASS: " + codec.getClass().getSimpleName());
            System.out.println("WRITE: " + codec.getCodecID());
            byte version = CodecRegistry.getCodecVersion(codec.getClass());
            System.out.println("Version: " + version);

            if (version > 0) {
                dataOutputStream.writeByte(codec.getCodecID());
                dataOutputStream.writeByte(version);
                codec.writeObject(dataOutputStream);
                System.out.println("----------------------------");
            } else {
                throw new IllegalArgumentException("Could not get version from codec " + codec.getClass().getSimpleName() + " (version > 0?)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

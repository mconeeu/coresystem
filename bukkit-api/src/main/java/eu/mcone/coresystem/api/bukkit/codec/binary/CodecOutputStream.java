package eu.mcone.coresystem.api.bukkit.codec.binary;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


public class CodecOutputStream implements Serializable {

    public CodecOutputStream() {
    }

    public void write(Codec<?, ?> codec, CodecSerializedCallback codecSerializedCallback) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        write(codec, dataOutputStream, codecSerializedCallback);
        codecSerializedCallback.finished(false, byteArrayOutputStream.toByteArray(), codec);
    }

    public void write(List<Codec<?, ?>> codecs, CodecSerializedCallback codecSerializedCallback) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeInt(codecs.size());
            for (Codec<?, ?> codec : codecs) {
                write(codec, dataOutputStream, codecSerializedCallback);
            }

            codecSerializedCallback.finished(false, byteArrayOutputStream.toByteArray(), codecs.toArray(new Codec[0]));
        } catch (IOException e) {
            codecSerializedCallback.error();
            e.printStackTrace();
        }
    }

    private void write(Codec<?, ?> codec, DataOutputStream dataOutputStream, CodecSerializedCallback codecSerializedCallback) {
        try {
            // codec version
            int codecVersion = Codec.BASE_VERSION;
            // codec specific version
            int specificVersion = CodecRegistry.getCodecVersion(codec.getClass());

            if (specificVersion > 0) {
                // write codec version
                dataOutputStream.writeByte(codecVersion);
                // write codec id
                dataOutputStream.writeShort(codec.getCodecID());
                //write encoder id
                dataOutputStream.writeShort(codec.getEncoderID());
                // write specific codec id
                dataOutputStream.writeByte(specificVersion);
                // write object
                codec.writeObject(dataOutputStream);
            } else {
                codecSerializedCallback.error();
                throw new IllegalArgumentException("Could not get version from codec " + codec.getClass().getSimpleName() + " (version > 0?)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

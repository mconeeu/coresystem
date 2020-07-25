package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.DeserializeCallback;
import eu.mcone.coresystem.api.bukkit.codec.Codec;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static eu.mcone.coresystem.bukkit.codec.CodecRegistry.getCodecVersion;

public class CodecInputStream {

    private final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry;

    public CodecInputStream(final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public DeserializeCallback deserialize(byte[] array) {
        try {
            if (array.length > 0) {
                List<Codec<?, ?>> deserializedCodecs = new ArrayList<>();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

                //Migration
                int migrated = 0;
                ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
                DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

                int codecID = dataInputStream.read();

                while (codecID != -1) {
                    Class<? extends Codec<?, ?>> codecClass = codecRegistry.getCodecByID((byte) codecID);
                    byte version = dataInputStream.readByte();
                    byte codecVersion = getCodecVersion(codecClass);
                    Codec<?, ?> codec = codecClass.newInstance();

                    codec.setCodecID((byte) codecID);
                    migratedDataOutput.writeByte(codec.getCodecID());
                    migratedDataOutput.writeByte(codecVersion);

                    if (version == codecVersion) {
                        codec.readObject(dataInputStream);
                        codec.writeObject(migratedDataOutput);
                    } else if (version < codecVersion) {
                        migrated++;
                        codec.migrate(dataInputStream, migratedDataOutput);
                    } else {
                        throw new IllegalStateException("Could not read Codec " + codecClass.getSimpleName() + ". This plugin is outdated!");
                    }

                    deserializedCodecs.add(codec);
                    codecID = dataInputStream.read();
                }

                DeserializeCallback callback;
                if (migrated == 0) {
                    callback = new DeserializeCallback(deserializedCodecs, null, 0);
                } else {
                    CoreSystem.getInstance().sendConsoleMessage("§cMigrating " + migrated + " Codec(s)");
                    callback = new DeserializeCallback(deserializedCodecs, migratedByteOutput.toByteArray(), migrated);
                }

                byteArrayInputStream.close();
                dataInputStream.close();

                migratedByteOutput.close();
                migratedDataOutput.close();

                return callback;
            }

            return null;
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}

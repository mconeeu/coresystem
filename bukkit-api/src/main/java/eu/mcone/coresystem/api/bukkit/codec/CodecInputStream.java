package eu.mcone.coresystem.api.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CoreSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodecInputStream {

    private final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry;

    public CodecInputStream(final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public SingleCodecCallback read(DataInputStream dataInputStream) {
        try {
            Codec<?, ?> obj = null;
            //Migration
            int migrated = 0;
            ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
            DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

            int codecID;
            if ((codecID = dataInputStream.read()) != 0) {
                Class<? extends Codec<?, ?>> codecClass = codecRegistry.getCodecByID((byte) codecID);
                byte version = dataInputStream.readByte();
                byte codecVersion = CodecRegistry.getCodecVersion(codecClass);
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

                obj = codec;
            }

            SingleCodecCallback callback;
            if (migrated == 0) {
                callback = new SingleCodecCallback(obj, null, 0);
            } else {
                CoreSystem.getInstance().sendConsoleMessage("§cMigrating " + migrated + " Codec(s)");
                callback = new SingleCodecCallback(obj, migratedByteOutput.toByteArray(), migrated);
            }

            migratedByteOutput.close();
            migratedDataOutput.close();
            return callback;
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MultipleCodecCallback readAsList(DataInputStream dataInputStream) {
        try {
            List<Codec<?, ?>> deserializedCodecs = new ArrayList<>();
            //Migration
            int migrated = 0;
            ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
            DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

            int size = dataInputStream.readInt();

            for (int i = 0; i < size; i++) {
                byte codecID = dataInputStream.readByte();
                Class<? extends Codec<?, ?>> codecClass = codecRegistry.getCodecByID(codecID);
                byte version = dataInputStream.readByte();
                byte codecVersion = CodecRegistry.getCodecVersion(codecClass);
                Codec<?, ?> codec = codecClass.newInstance();

                codec.setCodecID(codecID);
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
            }

            MultipleCodecCallback callback;
            if (migrated == 0) {
                callback = new MultipleCodecCallback(deserializedCodecs, null, 0);
            } else {
                CoreSystem.getInstance().sendConsoleMessage("§cMigrating " + migrated + " Codec(s)");
                callback = new MultipleCodecCallback(deserializedCodecs, migratedByteOutput.toByteArray(), migrated);
            }

            dataInputStream.close();

            migratedByteOutput.close();
            migratedDataOutput.close();

            return callback;
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MultipleCodecCallback readAsList(byte[] array) {
        if (array.length > 0) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

            return readAsList(dataInputStream);
        }

        return null;
    }
}

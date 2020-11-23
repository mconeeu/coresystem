package eu.mcone.coresystem.api.bukkit.codec.binary;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.codec.migration.MigrationCallback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CodecInputStream {

    private final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry;

    public CodecInputStream(final eu.mcone.coresystem.api.bukkit.codec.CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public void read(DataInputStream dataInputStream, CodecDeserializedCallback codecDeserializedCallback) {
        try {
            Codec<?, ?> deserializedCodec = null;
            boolean migrated = false;

            ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
            DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

            // read codec version (not codec specific)
            byte codecVersion = dataInputStream.readByte();

            short codecID;
            if ((codecID = dataInputStream.readShort()) != 0) {
                migratedDataOutput.writeShort(codecID);

                short encoderID = dataInputStream.readShort();
                migratedDataOutput.writeShort(encoderID);

                // get class from codec id
                Class<? extends Codec<?, ?>> codecClass = codecRegistry.getCodecByID(codecID);

                if (codecRegistry.getCodecMigration().checkForMigration(codecVersion, codecClass)) {
                    MigrationCallback callback = codecRegistry.getCodecMigration().migrate(codecClass, dataInputStream);
                    deserializedCodec = callback.getCodec();
                    migratedByteOutput.write(callback.getBinary());
                    migrated = true;
                } else {
                    // read codec version (codec specific)
                    byte codecStreamVersion = dataInputStream.readByte();
                    int specificCodecVersion = CodecRegistry.getCodecVersion(codecClass);
                    Codec<?, ?> codec = codecClass.newInstance();

                    // set id's
                    codec.setCodecID(codecID);
                    codec.setEncoderID(encoderID);

                    // write the codec id back
                    migratedDataOutput.writeByte(codecVersion);

                    if (codecStreamVersion == specificCodecVersion) {
                        // read codec first
                        codec.readObject(dataInputStream);

                        // write codec back
                        codec.writeObject(migratedDataOutput);
                    } else if (codecStreamVersion < specificCodecVersion) {
                        migrated = true;
                        // migrate codec and write it back to the data output
                        codec.migrate(dataInputStream, migratedDataOutput);
                    } else {
                        codecDeserializedCallback.error();
                        throw new IllegalStateException("Could not read Codec " + codecClass.getSimpleName() + ". This plugin is outdated!");
                    }

                    deserializedCodec = codec;
                }
            }

            codecDeserializedCallback.finished(migrated, migratedByteOutput.toByteArray(), deserializedCodec);

            migratedByteOutput.close();
            migratedDataOutput.close();
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readAsList(DataInputStream dataInputStream, CodecDeserializedCallback codecDeserializedCallback) {
        try {
            List<Codec<?, ?>> deserializedCodecs = new ArrayList<>();
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);

            ByteArrayOutputStream migratedByteOutput = new ByteArrayOutputStream();
            DataOutputStream migratedDataOutput = new DataOutputStream(migratedByteOutput);

            int size = dataInputStream.readInt();
            migratedDataOutput.writeInt(size);

            for (int i = 0; i < size; i++) {
                read(dataInputStream, new CodecDeserializedCallback() {
                    @Override
                    public void finished(boolean migrated, byte[] binary, Codec<?, ?>... codecs) {
                        try {
                            if (migrated) {
                                atomicBoolean.set(true);
                            }
                            // read() returns a single codec!
                            deserializedCodecs.add(codecs[0]);
                            migratedByteOutput.write(binary);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error() {
                        codecDeserializedCallback.error();
                    }
                });
            }

            codecDeserializedCallback.finished(atomicBoolean.get(), migratedByteOutput.toByteArray(), deserializedCodecs.toArray(new Codec[0]));

            dataInputStream.close();

            migratedByteOutput.close();
            migratedDataOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readAsList(byte[] array, CodecDeserializedCallback codecDeserializedCallback) {
        if (array.length > 0) {
            readAsList(new DataInputStream(new ByteArrayInputStream(array)), codecDeserializedCallback);
        } else {
            codecDeserializedCallback.error();
        }
    }
}

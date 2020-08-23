package eu.mcone.coresystem.api.core.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {
    public static UUID toUUID(DataInputStream dataInputStream) {
        try {
            long firstLong = dataInputStream.readLong();
            long secondLong = dataInputStream.readLong();
            return new UUID(firstLong, secondLong);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static UUID toUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static void toByteArray(UUID uuid, DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeLong(uuid.getMostSignificantBits());
            dataOutputStream.writeLong(uuid.getLeastSignificantBits());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}

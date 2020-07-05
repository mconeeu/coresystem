package eu.mcone.coresystem.api.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressUtils {

    public static byte[] compress(byte[] unCompressed) {
        try {
            Deflater deflater = new Deflater();
            deflater.setInput(unCompressed);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(unCompressed.length);
            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer); // returns the generated code... index
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] unCompress(byte[] compressed) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] buf = new byte[1024];
            int length;
            while ((length = byteArrayInputStream.read(buf, 0, buf.length)) >= 0) {
                byteArrayOutputStream.write(buf, 0, length);
            }

            byte[] compressedData = byteArrayOutputStream.toByteArray();

            //Uncompress
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);
            byteArrayOutputStream = new ByteArrayOutputStream(compressedData.length);
            while (!inflater.finished()) {
                int count = inflater.inflate(buf);
                byteArrayOutputStream.write(buf, 0, count);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (DataFormatException e) {
            e.printStackTrace();
        }

        return null;
    }
}

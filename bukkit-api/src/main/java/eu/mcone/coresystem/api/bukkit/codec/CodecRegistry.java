package eu.mcone.coresystem.api.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.codec.migration.CodecMigration;

import java.util.List;
import java.util.Map;

public interface CodecRegistry {

    CodecMigration getCodecMigration();

    void listeningForCodecs(boolean listening);

    boolean registerCodec(short codecID, Class<? extends Codec<?, ?>> codecClass, Class<?> triggerClass, short encoderID, Class<?> encoder);

    void unregisterCodec(CodecInformation information);

    void unregisterCodecs(CodecInformation... informations);

    void registerCodecListener(CodecListener... listeners);

    void unregisterCodecListener(CodecListener... listeners);

    Class<?> getEncoderClass(int encoderID);

    Class<?> getTriggerClass(int codecID);

    Class<? extends Codec<?, ?>> getCodecByID(int ID);

    Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByTriggerTyp(Class<?> triggerTyp);

    List<Class<? extends Codec<?, ?>>> getCodecsByTrigger(Class<?> triggerClass);

    Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByEncoder(Class<?> encodeClass);

    Class<? extends Codec<?, ?>> getCodecForClassName(String className);

    boolean existsCodec(Class<? extends Codec<?, ?>> codec);

    boolean hasCodec(Object object);

    static byte getCodecVersion(Class<?> clazz) {
        try {
            return clazz.getField("CODEC_VERSION").getByte(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

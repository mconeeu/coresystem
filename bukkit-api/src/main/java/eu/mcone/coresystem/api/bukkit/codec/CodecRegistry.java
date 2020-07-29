package eu.mcone.coresystem.api.bukkit.codec;

import java.util.List;
import java.util.Map;

public interface CodecRegistry {

    void listeningForCodecs(boolean listening);

    boolean registerCodec(byte codecID, Class<? extends Codec<?, ?>> codecClass, Class<?> triggerClass, byte encoderID, Class<?> encoder);

    void registerCodecListener(CodecListener... listeners);

    void unregisterCodecListener(CodecListener... listeners);

    Class<?> getEncoderClass(byte encoderID);

    Class<?> getTriggerClass(byte codecID);

    Class<? extends Codec<?, ?>> getCodecByID(byte ID);

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

package eu.mcone.coresystem.api.bukkit.codec;

import java.util.Map;

public interface CodecRegistry {

    void listeningForCodecs(boolean listening);

    void registerCodec(Class<?> clazz, Class<? extends Codec<?>> codec);

    void registerCodecListener(CodecListener... listeners);

    void unregisterCodecListener(CodecListener... listeners);

    <T> Class<? extends Codec<T>> getCodec(Class<?> typ, Object object);

    Map<Class<?>, Class<? extends Codec<?>>> getCodecs(Class<?> typ);

    Class<? extends Codec<?>> getCodecForClassName(String className);

    boolean existsCodec(Class<? extends Codec<?>> codec);

    boolean hasCodec(Object object);
}

package eu.mcone.coresystem.api.bukkit.codec;

import java.util.List;
import java.util.Map;

public interface CodecRegistry {

    void listeningForCodecs(boolean listening);

    boolean registerCodec(Class<?> clazz, Class<? extends Codec<?, ?>> codec);

    void registerCodecListener(CodecListener... listeners);

    void unregisterCodecListener(CodecListener... listeners);

    List<Class<? extends Codec<?, ?>>> getCodec(Class<?> typ, Object object);

    Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByCodec(Class<?> encodeClass);

    Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByEncoder(Class<?> encodeClass);

    Class<? extends Codec<?, ?>> getCodecForClassName(String className);

    boolean existsCodec(Class<? extends Codec<?, ?>> codec);

    boolean hasCodec(Object object);
}

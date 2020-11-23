package eu.mcone.coresystem.api.bukkit.codec;

public interface CodecListener {

    void onCodec(Codec<?, ?> codec, Object event, Object... objects);
}

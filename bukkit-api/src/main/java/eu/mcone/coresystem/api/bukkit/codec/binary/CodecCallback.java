package eu.mcone.coresystem.api.bukkit.codec.binary;

import eu.mcone.coresystem.api.bukkit.codec.Codec;

public interface CodecCallback {

    void finished(boolean migrated, byte[] binary, Codec<?, ?>... codecs);

    void error();
}

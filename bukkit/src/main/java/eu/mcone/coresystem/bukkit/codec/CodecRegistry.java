package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecListener;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.event.Event;

import javax.activation.UnsupportedDataTypeException;
import java.util.*;

@Getter
public class CodecRegistry implements eu.mcone.coresystem.api.bukkit.codec.CodecRegistry {

    private final CorePlugin instance;
    private final Map<Class<?>, List<Class<? extends Codec<?, ?>>>> codecs;
    @Getter
    private final List<CodecListener> listeners;
    @Getter
    private final GeneralCodecListener codecListener;

    public CodecRegistry(CorePlugin instance, boolean listening) {
        this.instance = instance;
        codecs = new HashMap<>();
        listeners = new ArrayList<>();
        codecListener = new GeneralCodecListener(this);

        if (listening) {
            codecListener.listening();
        }
    }

    public void listeningForCodecs(boolean listening) {
        if (listening) {
            codecListener.listening();
        } else {
            codecListener.unListening();
        }
    }

    public boolean registerCodec(Class<?> clazz, Class<? extends Codec<?, ?>> codec) {
        try {
            if (!existsCodec(codec)) {
                if (Packet.class.isAssignableFrom(clazz) || Event.class.isAssignableFrom(clazz)) {
                    if (codecs.containsKey(clazz)) {
                        codecs.get(clazz).add(codec);
                    } else {
                        codecs.put(clazz, new ArrayList<Class<? extends Codec<?, ?>>>() {{
                            add(codec);
                        }});
                    }

                    instance.sendConsoleMessage("§aRegistering packet Codec §f" + codec.getName());

                    if (codecListener.isListening()) {
                        codecListener.refresh();
                    }

                    return true;
                } else {
                    throw new UnsupportedDataTypeException("Unknown data typ " + clazz.getSimpleName());
                }
            } else {
                instance.sendConsoleMessage("§cCodec for class " + codec.getName() + " already registered!");
                return false;
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void registerCodecListener(CodecListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public void unregisterCodecListener(CodecListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    @SuppressWarnings("unchecked")
    public List<Class<? extends Codec<?, ?>>> getCodec(Class<?> typ, Object object) {
        try {
            if (typ.equals(Packet.class) || typ.equals(Event.class)) {
                for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codecList : codecs.entrySet()) {
                    if (codecList.getKey().getSimpleName().equalsIgnoreCase(object.getClass().getSimpleName()) && typ.isAssignableFrom(object.getClass())) {
                        return codecList.getValue();
                    }
                }
            } else {
                throw new UnsupportedDataTypeException("Unknown data typ " + typ.getSimpleName());
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByCodec(Class<?> encodeClass) {
        try {
            if (encodeClass.equals(Packet.class) || encodeClass.equals(Event.class)) {
                Map<Class<?>, List<Class<? extends Codec<?, ?>>>> found = new HashMap<>();
                for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codec : codecs.entrySet()) {
                    if (encodeClass.isAssignableFrom(codec.getKey())) {
                        found.put(codec.getKey(), codec.getValue());
                    }
                }

                return found;
            } else {
                throw new UnsupportedDataTypeException("Unknown data typ " + encodeClass.getSimpleName());
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByEncoder(Class<?> encoderClass) {
        Map<Class<?>, List<Class<? extends Codec<?, ?>>>> found = new HashMap<>();
        for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codec : codecs.entrySet()) {
            if (encoderClass.isAssignableFrom(codec.getKey())) {
                found.put(codec.getKey(), codec.getValue());
            }
        }

        return found;
    }

    public Class<? extends Codec<?, ?>> getCodecForClassName(String className) {
        for (List<Class<? extends Codec<?, ?>>> codecList : codecs.values()) {
            for (Class<? extends Codec<?, ?>> codec : codecList) {
                if (codec.getSimpleName().equalsIgnoreCase(className)) {
                    return codec;
                }
            }
        }

        return null;
    }

    public boolean existsCodec(Class<? extends Codec<?, ?>> codec) {
        for (List<Class<? extends Codec<?, ?>>> codecList : this.codecs.values()) {
            for (Class<? extends Codec<?, ?>> codecs : codecList) {
                if (codecs.getName().equalsIgnoreCase(codec.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasCodec(Object object) {
        for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codec : codecs.entrySet()) {
            if (codec.getKey().getSimpleName().equalsIgnoreCase(object.getClass().getSimpleName())) {
                return true;
            }
        }

        return false;
    }
}

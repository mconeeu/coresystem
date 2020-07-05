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
    private final Map<Class<?>, Class<? extends Codec<?>>> codecs;
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

    public void registerCodec(Class<?> clazz, Class<? extends Codec<?>> codec) {
        try {
            if (!existsCodec(codec)) {
                if (Packet.class.isAssignableFrom(clazz) || Event.class.isAssignableFrom(clazz)) {
                    codecs.put(clazz, codec);
                    instance.sendConsoleMessage("§aRegistering packet Codec §f" + codec.getName());

                    if (codecListener.isListening()) {
                        codecListener.refresh();
                    }
                } else {
                    throw new UnsupportedDataTypeException("Unknown data typ " + clazz.getSimpleName());
                }
            } else {
                instance.sendConsoleMessage("§cCodec for class " + codec.getName() + " already registered!");
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }
    }

    public void registerCodecListener(CodecListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public void unregisterCodecListener(CodecListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends Codec<T>> getCodec(Class<?> typ, Object object) {
        try {
            if (typ.equals(Packet.class) || typ.equals(Event.class)) {
                for (Map.Entry<Class<?>, Class<? extends Codec<?>>> codec : codecs.entrySet()) {
                    if (codec.getKey().getSimpleName().equalsIgnoreCase(object.getClass().getSimpleName()) && typ.isAssignableFrom(object.getClass())) {
                        return (Class<? extends Codec<T>>) codec.getValue();
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

    public Map<Class<?>, Class<? extends Codec<?>>> getCodecs(Class<?> typ) {
        try {
            if (typ.equals(Packet.class) || typ.equals(Event.class)) {
                Map<Class<?>, Class<? extends Codec<?>>> found = new HashMap<>();
                for (Map.Entry<Class<?>, Class<? extends Codec<?>>> codec : codecs.entrySet()) {
                    if (typ.isAssignableFrom(codec.getKey())) {
                        System.out.println(codec.getKey().getSimpleName());
                        found.put(codec.getKey(), codec.getValue());
                    }
                }

                return found;
            } else {
                throw new UnsupportedDataTypeException("Unknown data typ " + typ.getSimpleName());
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Class<? extends Codec<?>> getCodecForClassName(String className) {
        for (Class<? extends Codec<?>> codec : codecs.values()) {
            if (codec.getSimpleName().equalsIgnoreCase(className)) {
                return codec;
            }
        }

        return null;
    }

    public boolean existsCodec(Class<? extends Codec<?>> codec) {
        for (Class<? extends Codec<?>> registered : this.codecs.values()) {
            if (registered.getName().equalsIgnoreCase(codec.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean hasCodec(Object object) {
        for (Map.Entry<Class<?>, Class<? extends Codec<?>>> codec : codecs.entrySet()) {
            if (codec.getKey().getSimpleName().equalsIgnoreCase(object.getClass().getSimpleName())) {
                return true;
            }
        }

        return false;
    }
}

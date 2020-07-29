package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecListener;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.event.Event;

import javax.activation.UnsupportedDataTypeException;
import java.util.*;

@Getter
public class CodecRegistry implements eu.mcone.coresystem.api.bukkit.codec.CodecRegistry {

    private final CorePlugin instance;
    private final Map<Class<?>, List<Class<? extends Codec<?, ?>>>> codecs;
    private final Map<Byte, Class<? extends Codec<?, ?>>> codecIDs;
    private final Map<Byte, Class<?>> encoderIDs;
    @Getter
    private final List<CodecListener> listeners;
    @Getter
    private final GeneralCodecListener codecListener;

    public CodecRegistry(CorePlugin instance, boolean listening) {
        this.instance = instance;
        codecs = new HashMap<>();
        codecIDs = new HashMap<>();
        encoderIDs = new HashMap<>();
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

    public boolean registerCodec(byte codecID, Class<? extends Codec<?, ?>> codecClass, Class<?> triggerClass, byte encoderID, Class<?> encoder) {
        try {
            if (!existsCodec(codecClass)) {
                if (Packet.class.isAssignableFrom(triggerClass) || Event.class.isAssignableFrom(triggerClass)) {
                    if (codecIDs.containsKey(codecID)) {
                        instance.sendConsoleMessage("§cCodec id " + codecID + " already registered, codec class " + codecIDs.get(codecID).getSimpleName());
                        return false;
                    }

                    if (!encoderIDs.containsKey(encoderID)) {
                        encoderIDs.put(encoderID, encoder);
                    }

                    if (codecs.containsKey(triggerClass)) {
                        codecs.get(triggerClass).add(codecClass);
                    } else {
                        codecIDs.put(codecID, codecClass);
                        codecs.put(triggerClass, new ArrayList<Class<? extends Codec<?, ?>>>() {{
                            add(codecClass);
                        }});
                    }

                    instance.sendConsoleMessage("§aRegistering packet Codec §f" + codecClass.getSimpleName());

                    if (codecListener.isListening()) {
                        codecListener.refresh();
                    }

                    return true;
                } else {
                    throw new UnsupportedDataTypeException("Unknown data typ " + triggerClass.getSimpleName());
                }
            } else {
                instance.sendConsoleMessage("§cCodec for class " + codecClass.getName() + " already registered!");
                return false;
            }
        } catch (UnsupportedDataTypeException e) {
            BukkitCoreSystem.getInstance().sendConsoleMessage("Could not find encoderID in class " + encoder.getSimpleName() + " StackTrace: " + e.getMessage());
        }

        return false;
    }

    public void registerCodecListener(CodecListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public void unregisterCodecListener(CodecListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    public Class<?> getEncoderClass(byte ID) {
        return encoderIDs.get(ID);
    }

    public Class<?> getTriggerClass(byte ID) {
        Class<? extends Codec<?, ?>> codec = getCodecByID(ID);

        if (codec != null) {
            for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> entry : codecs.entrySet()) {
                for (Class<? extends Codec<?, ?>> codecEntry : entry.getValue()) {
                    if (codecEntry == codec) {
                        return entry.getKey();
                    }
                }
            }
        }

        return null;
    }

    public Class<? extends Codec<?, ?>> getCodecByID(byte ID) {
        return codecIDs.getOrDefault(ID, null);
    }

    public Map<Class<?>, List<Class<? extends Codec<?, ?>>>> getCodecsByTriggerTyp(Class<?> triggerTyp) {
        try {
            if (triggerTyp.equals(Packet.class) || triggerTyp.equals(Event.class)) {
                Map<Class<?>, List<Class<? extends Codec<?, ?>>>> found = new HashMap<>();
                for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codecEntry : codecs.entrySet()) {
                    if (triggerTyp.isAssignableFrom(codecEntry.getKey())) {
                        found.put(codecEntry.getKey(), codecEntry.getValue());
                    }
                }

                return found;
            } else {
                throw new UnsupportedDataTypeException("Unknown data typ " + triggerTyp.getSimpleName());
            }
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Class<? extends Codec<?, ?>>> getCodecsByTrigger(Class<?> trigger) {
        try {
            if (Packet.class.isAssignableFrom(trigger) || Event.class.isAssignableFrom(trigger)) {
                for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> codec : codecs.entrySet()) {
                    if (trigger.isAssignableFrom(codec.getKey())) {
                        return codec.getValue();
                    }
                }

                return null;
            } else {
                throw new UnsupportedDataTypeException("Unknown data typ " + trigger.getSimpleName());
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

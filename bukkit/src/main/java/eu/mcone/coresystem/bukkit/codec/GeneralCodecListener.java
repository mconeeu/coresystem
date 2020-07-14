package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.PacketListener;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralCodecListener {

    private final CodecRegistry codecRegistry;

    private PacketListener packetListener;
    private final Map<Class<? extends Event>, RegisteredListener> listener;
    @Getter
    private boolean listening = false;

    public GeneralCodecListener(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
        this.listener = new HashMap<>();
    }

    public void listening() {
        if (!listening) {
            listening = true;

            //Packets
            packetListener = new PacketListener() {
                @Override
                public void onPacketIn(Player player, Packet<?> packet) {
                    if (codecRegistry.hasCodec(packet)) {
                        try {
                            for (Class<?> codecClass : codecRegistry.getCodec(Packet.class, packet)) {
                                Codec codec = (Codec) codecClass.newInstance();
                                if (codec.decode(player, packet) != null) {
                                    callListeners(codec, codec);
                                }
                            }
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onPacketOut(Player player, Packet<?> packet) {
                    if (codecRegistry.hasCodec(packet)) {
                        try {
                            for (Class<?> codecClass : codecRegistry.getCodec(Packet.class, packet)) {
                                Codec codec = (Codec) codecClass.newInstance();
                                if (codec.decode(player, packet) != null) {
                                    callListeners(codec, codec);
                                }
                            }
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            CoreSystem.getInstance().getPacketManager().registerPacketListener(packetListener);

            refresh();
        }
    }

    public void refresh() {
        if (listening) {
            for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> entry : codecRegistry.getCodecsByCodec(Event.class).entrySet()) {
                Class<? extends Event> eventClass = (Class<? extends Event>) entry.getKey();

                if (!listener.containsKey(eventClass)) {
                    HandlerList handlerList = getHandlerList(eventClass);

                    if (handlerList != null) {
                        RegisteredListener registeredListener = new RegisteredListener(null, (listener, event) -> {
                            try {
                                for (Class<?> codecClass : codecRegistry.getCodec(Event.class, event)) {
                                    Codec codec = (Codec) codecClass.newInstance();
                                    if (codec.decode(null, event) != null) {
                                        callListeners(codec, codec);
                                    }
                                }
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }, EventPriority.NORMAL, codecRegistry.getInstance(), false);
                        handlerList.register(registeredListener);
                        this.listener.put(eventClass, registeredListener);
                    }
                }
            }
        }
    }

    private HandlerList getHandlerList(Class<? extends Event> event) {
        try {
            SimplePluginManager pluginManager = (SimplePluginManager) Bukkit.getPluginManager();
            Method method = pluginManager.getClass().getDeclaredMethod("getEventListeners", event.getClass());
            method.setAccessible(true);
            return (HandlerList) method.invoke(pluginManager, event);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void callListeners(Codec<?, ?> codec, Object... args) {
        for (eu.mcone.coresystem.api.bukkit.codec.CodecListener listener : codecRegistry.getListeners()) {
            listener.onCodec(codec, args);
        }
    }

    public void unListening() {
        listening = false;
        CoreSystem.getInstance().getPacketManager().unregisterPacketListener(packetListener);

        for (Map.Entry<Class<? extends Event>, RegisteredListener> entry : listener.entrySet()) {
            HandlerList handlerList = getHandlerList(entry.getKey());

            if (handlerList != null) {
                handlerList.unregister(entry.getValue());
            }
        }
    }
}

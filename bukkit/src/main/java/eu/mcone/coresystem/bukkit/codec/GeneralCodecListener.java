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
                            Codec codec = codecRegistry.getCodec(Packet.class, packet).newInstance();
                            codec.decode(null, packet);
                            callListeners(codec);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onPacketOut(Player player, Packet<?> packet) {
                    if (codecRegistry.hasCodec(packet)) {
                        try {
                            Codec codec = codecRegistry.getCodec(Packet.class, packet).newInstance();
                            codec.decode(null, packet);
                            callListeners(codec);
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
            for (Map.Entry<Class<?>, Class<? extends Codec<?>>> entry : codecRegistry.getCodecs(Event.class).entrySet()) {
                Class<? extends Event> eventClass = (Class<? extends Event>) entry.getKey();

                if (!listener.containsKey(eventClass)) {
                    HandlerList handlerList = getHandlerList(eventClass);

                    if (handlerList != null) {
                        RegisteredListener registeredListener = new RegisteredListener(null, (listener, event) -> {
                            try {
                                Codec codec = codecRegistry.getCodec(Event.class, event).newInstance();
                                codec.decode(null, event);
                                callListeners(codec);
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

    private void callListeners(Codec<?> codec) {
        for (eu.mcone.coresystem.api.bukkit.codec.CodecListener listener : codecRegistry.getListeners()) {
            listener.onCodec(codec);
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

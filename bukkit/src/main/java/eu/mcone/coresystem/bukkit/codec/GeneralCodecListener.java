package eu.mcone.coresystem.bukkit.codec;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecListener;
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

@SuppressWarnings({"unchecked", "rawtypes"})
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
                            List<Class<? extends Codec<?, ?>>> codecs = codecRegistry.getCodecsByTrigger(packet.getClass());
                            if (codecs != null) {
                                for (Class<?> codecClass : codecs) {
                                    Codec codec = (Codec) codecClass.newInstance();
                                    Object[] args = codec.decode(player, packet);
                                    if (args != null) {
                                        callListeners(codec, args);
                                    }
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
                            List<Class<? extends Codec<?, ?>>> codecs = codecRegistry.getCodecsByTrigger(packet.getClass());
                            if (codecs != null) {
                                for (Class<?> codecClass : codecs) {
                                    Codec codec = (Codec) codecClass.newInstance();
                                    Object[] args = codec.decode(player, packet);
                                    if (args != null) {
                                        callListeners(codec, args);
                                    }
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
        unRegisterListeners();

        if (listening) {
            for (Map.Entry<Class<?>, List<Class<? extends Codec<?, ?>>>> entry : codecRegistry.getCodecsByTriggerTyp(Event.class).entrySet()) {
                Class<? extends Event> eventClass = (Class<? extends Event>) entry.getKey();

                if (!listener.containsKey(eventClass)) {
                    HandlerList handlerList = getHandlerList(eventClass);

                    if (handlerList != null) {
                        RegisteredListener registeredListener = new RegisteredListener(null, (listener, event) -> {
                            if (!eventClass.isAssignableFrom(event.getClass()))
                                return;

                            try {
                                for (Class<? extends Codec<?, ?>> codecClass : entry.getValue()) {
                                    Codec codec = codecClass.newInstance();
                                    Object[] args = codec.decode(null, event);
                                    if (args != null && args.length > 0) {
                                        callListeners(codec, args);
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

    private void callListeners(Codec<?, ?> codec, Object[] args) {
        for (CodecListener listener : codecRegistry.getListeners()) {
//            System.out.println("Listener");
            listener.onCodec(codec, args);
        }
    }

    public void unRegisterListeners() {
        for (Map.Entry<Class<? extends Event>, RegisteredListener> entry : listener.entrySet()) {
            HandlerList handlerList = getHandlerList(entry.getKey());

            if (handlerList != null) {
                handlerList.unregister(entry.getValue());
            }
        }

        listener.clear();
    }

    public void unListening() {
        listening = false;
        CoreSystem.getInstance().getPacketManager().unregisterPacketListener(packetListener);
        unRegisterListeners();
    }
}

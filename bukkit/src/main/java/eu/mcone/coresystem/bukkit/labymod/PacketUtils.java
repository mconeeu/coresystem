/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.labymod;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class created by qlow | Jan
 */
public class PacketUtils {

    private String version;

    private Class<?> packetClass;

    private Class<?> packetPlayOutCustomPayloadClass;
    private Constructor<?> customPayloadConstructor;
    private boolean customPayloadHasBytes;

    private Class<?> packetDataSerializerClass;
    private Constructor<?> packetDataSerializerConstructor;

    private Method getHandleMethod;
    private Field playerConnectionField;
    @Getter
    private Field networkManagerField;

    PacketUtils() {
        this.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        try {
            this.packetClass = getNmsClass("Packet");
            this.packetPlayOutCustomPayloadClass = getNmsClass("PacketPlayOutCustomPayload");
            this.networkManagerField = getNmsClass("PlayerConnection").getDeclaredField("networkManager");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (this.packetPlayOutCustomPayloadClass != null) {
            for (Constructor<?> constructors : packetPlayOutCustomPayloadClass.getDeclaredConstructors()) {
                if (constructors.getParameterTypes().length == 2 && constructors.getParameterTypes()[1] == byte[].class) {
                    customPayloadHasBytes = true;
                    customPayloadConstructor = constructors;
                } else if (constructors.getParameterTypes().length == 2 && constructors.getParameterTypes()[1].getSimpleName().equals("PacketDataSerializer")) {
                    customPayloadConstructor = constructors;
                }
            }

            if (!customPayloadHasBytes) {
                try {
                    packetDataSerializerClass = getNmsClass("PacketDataSerializer");
                    packetDataSerializerConstructor = packetDataSerializerClass.getDeclaredConstructor(ByteBuf.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    CoreSystem.getInstance().getLogger().severe("Couldn't find a valid constructor for PacketPlayOutCustomPayload. Disabling the plugin.");
                }
            }
        }
    }

    /**
     * Gets the player's nms-handle
     *
     * @param player the bukkit-player
     * @return the nms-handle
     */
    private Object getPlayerHandle(Player player) {
        try {
            if (getHandleMethod == null)
                getHandleMethod = player.getClass().getMethod("getHandle");

            // Getting the player's nms-handle
            return getHandleMethod.invoke(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the player's connection
     *
     * @param nmsPlayer the player's nms-handle
     * @return the player-connection
     */
    private Object getPlayerConnection(Object nmsPlayer) {
        try {
            if (playerConnectionField == null)
                playerConnectionField = nmsPlayer.getClass().getField("playerConnection");

            // Getting the player's connection
            return playerConnectionField.get(nmsPlayer);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sends a packet to the given player
     *
     * @param player the player the packet should be sent to
     * @param packet the packet that should be sent to the player
     */
    void sendPacket(Player player, Object packet) {
        try {
            // Getting the player's nms-handle
            Object nmsPlayer = getPlayerHandle(player);

            // Getting the player's connection
            Object playerConnection = getPlayerConnection(nmsPlayer);

            // Sending the packet
            playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets a constructed plugin message packet
     *
     * @param channel the channel-name
     * @param bytes   the bytes that should be sent with the packet
     * @return a plugin-message packet
     */
    Object getPluginMessagePacket(String channel, byte[] bytes) {
        try {
            return customPayloadConstructor.newInstance(channel, customPayloadHasBytes ? bytes : packetDataSerializerConstructor.newInstance(Unpooled.wrappedBuffer(bytes)));
        } catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            CoreSystem.getInstance().getLogger().severe("Couldn't construct a custom-payload packet (Channel: " + channel + "):");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets a nms-class
     *
     * @param nmsClassName the nms-class name
     * @return the multi-version compatible name of the class including the package
     * @throws ClassNotFoundException if the class wasn't found
     */
    private Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + "." + nmsClassName);
    }

    /**
     * Sets a field's value
     *
     * @param targetObject the target object
     * @param fieldName    the field's name
     * @param value        the value
     */
    public void setField(Object targetObject, String fieldName, Object value) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(targetObject, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

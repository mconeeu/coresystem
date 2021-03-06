/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ReflectionManager {

    public static void setValue(Object packet, String name, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T getValue(Object obj, String name, Class<T> t) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return t.cast(field.get(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getValueFromSuper(Object obj, String name, Class<T> t) {
        try {
            Field field = obj.getClass().getSuperclass().getDeclaredField(name);
            field.setAccessible(true);
            return t.cast(field.get(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String field) {
        if (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(field);
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static EntityPlayer getNMSPlayer(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import net.minecraft.server.v1_13_R2.EntityPlayer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
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

	public static EntityPlayer getNMSPlayer(Player p) {
		return ((CraftPlayer) p).getHandle();
	}

}

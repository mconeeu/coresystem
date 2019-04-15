/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.util;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.util.CooldownSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreCooldownSystem implements CooldownSystem {

    private final Map<Class<?>, Integer> customCooldown = new HashMap<>();
    private final Map<Class<?>, Map<UUID, Long>> classMap = new HashMap<>();

    @Override
    public boolean addAndCheck(GlobalCoreSystem instance, Class<?> clazz, UUID uuid) {
        if (canExecute(instance, clazz, uuid)) {
            addPlayer(uuid, clazz);
            return true;
        }

        return false;
    }

    @Override
    public void addPlayer(UUID uuid, Class<?> clazz) {
        if (classMap.containsKey(clazz)) {
            classMap.get(clazz).put(uuid, System.currentTimeMillis() / 1000);
        } else {
            classMap.put(clazz, new HashMap<UUID, Long>(){{put(uuid, System.currentTimeMillis() / 1000);}});
        }
    }

    @Override
    public void setCustomCooldownFor(Class<?> clazz, int cooldown) {
        customCooldown.put(clazz, cooldown);
    }

    public boolean canExecute(GlobalCoreSystem instance, Class<?> clazz, UUID uuid) {
        GlobalCorePlayer p = instance.getGlobalCorePlayer(uuid);

        if (p.hasPermission("system.bungee.cooldown")) {
            return true;
        } else if (!classMap.containsKey(clazz)) {
            return true;
        } else if (!classMap.get(clazz).containsKey(uuid)) {
            return true;
        } else if (classMap.get(clazz).get(uuid) < ((System.currentTimeMillis() / 1000) - customCooldown.getOrDefault(clazz, DEFAULT_COOLDOWN))) {
            return true;
        } else {
            p.sendMessage("§8[§7§l!§8] §fSystem §8» §4Bitte warte noch "+(classMap.get(clazz).get(uuid) - ((System.currentTimeMillis() / 1000) - customCooldown.getOrDefault(clazz, DEFAULT_COOLDOWN)))+" Sekunden bevor du diesen Befehl wieder ausführst!");
            return false;
        }
    }

}

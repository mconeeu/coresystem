/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.util;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownSystem implements eu.mcone.coresystem.api.core.util.CooldownSystem {

    private Map<Class<?>, Map<UUID, Long>> cmds = new HashMap<>();

    public boolean addAndCheck(GlobalCoreSystem instance, Class<?> cmd, UUID uuid) {
        if (canExecute(instance, cmd, uuid)) {
            addPlayer(uuid, cmd);
            return true;
        }

        return false;
    }

    public void addPlayer(UUID uuid, Class<?> clazz) {
        if (cmds.containsKey(clazz)) {
            cmds.get(clazz).put(uuid, System.currentTimeMillis() / 1000);
        } else {
            cmds.put(clazz, new HashMap<UUID, Long>(){{put(uuid, System.currentTimeMillis() / 1000);}});
        }
    }

    public boolean canExecute(GlobalCoreSystem instance, Class<?> clazz, UUID uuid) {
        GlobalCorePlayer p = instance.getGlobalCorePlayer(uuid);
        if (p.hasPermission("system.bungee.cooldown")) {
            return true;
        } else if (cmds.getOrDefault(clazz, Collections.emptyMap()).getOrDefault(p.getUuid(), (System.currentTimeMillis() / 1000) - 3) < ((System.currentTimeMillis() / 1000) - 2)) {
            return true;
        } else {
            p.sendMessage("§8[§7§l!§8] §fSystem §8» §4Bitte warte einen Moment bevor du diesen Befehl wieder ausführst!");
            return false;
        }
    }

}

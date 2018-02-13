/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownSystem {

    private Map<Class<?>, Map<UUID, Long>> cmds = new HashMap<>();

    public void addPlayer(UUID uuid, Class<?> cmd) {
        cmds.put(cmd, new HashMap<UUID, Long>(){{put(uuid, System.currentTimeMillis() / 1000);}});
    }

    public boolean canExecute(Class<?> cmd, ProxiedPlayer p) {
        if (p.hasPermission("system.bungee.cooldown")) {
            return true;
        } else if (cmds.getOrDefault(cmd, new HashMap<>()).getOrDefault(p.getUniqueId(), (System.currentTimeMillis() / 1000) - 3) < ((System.currentTimeMillis() / 1000) - 2)) {
            return true;
        } else {
            p.sendMessage("§8[§7§l!§8] §fSystem §8» §4Bitte warte einen Moment bevor du diesen Befehl wieder ausführst!");
            return false;
        }
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import eu.mcone.coresystem.bukkit.npc.entity.PlayerCoreNpc;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public enum NpcType {

    PLAYER(EntityType.PLAYER, PlayerCoreNpc.class);

    private EntityType type;
    private Class<? extends CoreNPC> npcClass;

    NpcType(EntityType type, Class<? extends CoreNPC> npcClass) {
        this.type = type;
        this.npcClass = npcClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends CoreNPC> T construct(NpcData data, NpcVisibilityMode visibilityMode, Player... players) {
        try {
            Constructor constructor = npcClass.getDeclaredConstructor(NpcData.class, NpcVisibilityMode.class, Player[].class);
            constructor.setAccessible(true);

            return (T) constructor.newInstance(data, visibilityMode, players);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NpcType getNpcTypeByEntity(EntityType type) {
        for (NpcType t : values()) {
            if (t.getType().equals(type)) {
                return t;
            }
        }
        return null;
    }

}

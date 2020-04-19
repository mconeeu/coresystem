/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.bukkit.npc.entity.PigCoreNPC;
import eu.mcone.coresystem.bukkit.npc.entity.PlayerCoreNpc;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public enum NpcType {

    PLAYER(EntityType.PLAYER, PlayerCoreNpc.class),
    PIG(EntityType.PIG, PigCoreNPC.class);

    private final EntityType type;
    private final Class<? extends CoreNPC<?, ?>> npcClass;

    NpcType(EntityType type, Class<? extends CoreNPC<?, ?>> npcClass) {
        this.type = type;
        this.npcClass = npcClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends CoreNPC<?, ?>> T construct(NpcData data, ListMode visibilityMode, Player... players) {
        try {
            Constructor<?> constructor = npcClass.getDeclaredConstructor(NpcData.class, ListMode.class, Player[].class);
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

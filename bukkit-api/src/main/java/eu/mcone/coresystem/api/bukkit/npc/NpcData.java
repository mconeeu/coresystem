/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import com.google.gson.JsonElement;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.EntityType;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public final class NpcData {

    private EntityType type;
    private String name, displayname;
    private CoreLocation location;
    private JsonElement entityData;

    /**
     * Creates an NpcData Object for constructing a NPC via CoreSystem#constructNpc()
     * @param type the entity type of the NPC: currently supported types are PLAYER
     * @param name the config name of the NPC (must be unique per world)
     * @param displayname the displayname of the NPC (must not be longer than 16 chars, including color code chars)
     * @param location the Location where the NPC should appear (including yaw & pitch)
     * @param entityData the entity specific Data (i.e. for PLAYER: PlayerNpcData.class)
     */
    public <T extends AbstractNpcData> NpcData(EntityType type, String name, String displayname, CoreLocation location, T entityData) {
        this.type = type;
        this.name = name;
        this.displayname = displayname;
        this.location = location;
        this.entityData = CoreSystem.getInstance().getGson().toJsonTree(entityData, entityData.getClass());
    }

    public void updateData(NpcData data) {
        this.type = data.type;
        this.name = data.name;
        this.location = data.location;
        this.entityData = data.entityData;
    }

}

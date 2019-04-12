/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import com.google.gson.JsonElement;
import eu.mcone.coresystem.api.bukkit.npc.data.EntityNpcData;
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

    public <T extends EntityNpcData> NpcData(EntityType type, String name, String displayname, CoreLocation location, T entityData) {
        this.type = type;
        this.name = name;
        this.displayname = displayname;
        this.location = location;
        this.entityData = NpcModule.getInstance().getCoreSystem().getGson().toJsonTree(entityData, entityData.getClass());
    }

    public void updateData(NpcData data) {
        this.type = data.type;
        this.name = data.name;
        this.location = data.location;
        this.entityData = data.entityData;
    }

}

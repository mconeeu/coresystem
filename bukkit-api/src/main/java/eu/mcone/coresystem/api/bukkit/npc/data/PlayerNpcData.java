/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.data;

import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerNpcData extends AbstractNpcData {

    private String skinName = "MHF_Question", tablistName = "";
    private SkinInfo.SkinType skinType = SkinInfo.SkinType.PLAYER;
    private boolean visibleOnTab = false, sleeping = false, sleepWithBed = false;
    private Map<EquipmentPosition, ItemStack> equipment = new HashMap<>();

    @Override
    public String toString() {
        return "PlayerNpcData{" +
                "skinName='" + skinName + '\'' +
                ", tablistName='" + tablistName + '\'' +
                ", skinType=" + skinType +
                ", visibleOnTab=" + visibleOnTab +
                ", sleeping=" + sleeping +
                ", sleepWithBed=" + sleepWithBed +
                ", equipment=" + equipment +
                '}';
    }
}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.data;

import eu.mcone.coresystem.api.core.player.SkinInfo;
import lombok.*;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ToString
public class PlayerNpcData extends AbstractNpcData {

    @Builder.Default
    private String skinName = "MHF_Question", tablistName = "";
    @Builder.Default
    private SkinInfo.SkinType skinType = SkinInfo.SkinType.PLAYER;
    @Builder.Default
    private boolean visibleOnTab = false, sleeping = false, sleepWithBed = false;
    @Builder.Default
    private Map<EnumItemSlot, ItemStack> equipment = new HashMap<>();

}

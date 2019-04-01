/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.api.npc.enums;

import lombok.Getter;

public enum NpcState {

    MINECART_RESET_SPAWN_TIMER(1),
    LIVING_ENTITY_HURT(2),
    LIVING_ENTITY_DEAD(3),
    IRON_GOLEM_THROW_HANDS_UP(4),
    TAMABLE_TAMING(6),
    TAMABLE_TAMED(7),
    WOLF_SHAKE_OFF_WATER(8),
    EATING_ACCEPTED(9),
    SHEEP_EAT_GRASS(10),
    TNT_PLAY_IGNITE(10),
    IRON_GOLEM_HAND_OVER_ROSE(11),
    VILLAGER_MATING_HEARTS(12),
    VILLAGER_ANGRY(13),
    VILLAGER_HAPPY(14),
    WITCH_MAGIC(15),
    ZOMBIE_TO_VILLAGER_SOUND(16),
    FIREWORK_EXPLODE(17),
    ANIMALS_IN_LOVE_HEARTS(18),
    SQUID_RESET_ROTATION(19),
    SPAWN_EXPLOSION(20),
    GUARDIAN_SOUND(21),
    PLAYER_ENABLE_REDUCED_DEBUG(22),
    PLAYER_DISABLE_REDUCED_DEBUG(23);

    @Getter
    private int id;

    NpcState(int id) {
        this.id = id;
    }

}

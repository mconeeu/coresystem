package eu.mcone.coresystem.api.bukkit.sound;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public enum DefaultSound {

    CLICK(Sound.CHICKEN_EGG_POP),
    TICK(Sound.CLICK),
    DONE(Sound.ORB_PICKUP),
    SUCCESS(Sound.LEVEL_UP),
    ERROR(Sound.NOTE_BASS),
    SAVE(Sound.ANVIL_LAND),
    CANCEL(Sound.ANVIL_BREAK),
    CHANGE(Sound.BLAZE_HIT),
    EQUIP(Sound.HORSE_SADDLE),
    TELEPORT(Sound.ENDERMAN_TELEPORT),
    DEATH(Sound.VILLAGER_HIT),
    EPIC(Sound.WITHER_DEATH);

    private final Sound sound;

    DefaultSound(Sound sound) {
        this.sound = sound;
    }

}

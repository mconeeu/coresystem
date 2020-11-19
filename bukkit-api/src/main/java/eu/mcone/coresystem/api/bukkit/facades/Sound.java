package eu.mcone.coresystem.api.bukkit.facades;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.sound.SoundManager;
import org.bukkit.entity.Player;

public final class Sound {

    private static final SoundManager MANAGER = CoreSystem.getInstance().getSoundManager();

    public static void click(Player p) {
        MANAGER.playClick(p);
    }

    public static void tick(Player p) {
        MANAGER.playTick(p);
    }

    public static void done(Player p) {
        MANAGER.playDone(p);
    }

    public static void success(Player p) {
        MANAGER.playSuccess(p);
    }

    public static void error(Player p) {
        MANAGER.playError(p);
    }

    public static void save(Player p) {
        MANAGER.playSave(p);
    }

    public static void cancel(Player p) {
        MANAGER.playCancel(p);
    }

    public static void change(Player p) {
        MANAGER.playChange(p);
    }

    public static void equip(Player p) {
        MANAGER.playEquip(p);
    }

    public static void teleport(Player p) {
        MANAGER.playTeleport(p);
    }

    public static void death(Player p) {
        MANAGER.playDeath(p);
    }

    public static void epic(Player p) {
        MANAGER.playEpic(p);
    }


    public static void play(Player p, org.bukkit.Sound sound) {
        MANAGER.play(p, sound);
    }

}

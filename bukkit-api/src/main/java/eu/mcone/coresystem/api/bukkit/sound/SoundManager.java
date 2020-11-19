package eu.mcone.coresystem.api.bukkit.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public interface SoundManager {
    void playClick(Player p);

    void playTick(Player p);

    void playDone(Player p);

    void playSuccess(Player p);

    void playError(Player p);

    void playSave(Player p);

    void playCancel(Player p);

    void playChange(Player p);

    void playEquip(Player p);

    void playTeleport(Player p);

    void playDeath(Player p);

    void playEpic(Player p);

    void play(Player p, Sound sound);
}

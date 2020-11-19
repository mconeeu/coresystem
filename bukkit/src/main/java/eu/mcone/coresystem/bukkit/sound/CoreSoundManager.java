package eu.mcone.coresystem.bukkit.sound;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.sound.DefaultSound;
import eu.mcone.coresystem.api.bukkit.sound.SoundManager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CoreSoundManager implements SoundManager {

    private final BukkitCoreSystem system;

    @Override
    public void playClick(Player p) {
        doPlaySound(p, DefaultSound.CLICK.getSound());
    }

    @Override
    public void playTick(Player p) {
        doPlaySound(p, DefaultSound.TICK.getSound());
    }

    @Override
    public void playDone(Player p) {
        doPlaySound(p, DefaultSound.DONE.getSound());
    }

    @Override
    public void playSuccess(Player p) {
        doPlaySound(p, DefaultSound.SUCCESS.getSound());
    }

    @Override
    public void playError(Player p) {
        doPlaySound(p, DefaultSound.ERROR.getSound());
    }

    @Override
    public void playSave(Player p) {
        doPlaySound(p, DefaultSound.SAVE.getSound());
    }

    @Override
    public void playCancel(Player p) {
        doPlaySound(p, DefaultSound.CANCEL.getSound());
    }

    @Override
    public void playChange(Player p) {
        doPlaySound(p, DefaultSound.CHANGE.getSound());
    }

    @Override
    public void playEquip(Player p) {
        doPlaySound(p, DefaultSound.EQUIP.getSound());
    }

    @Override
    public void playTeleport(Player p) {
        doPlaySound(p, DefaultSound.TELEPORT.getSound());
    }

    @Override
    public void playDeath(Player p) {
        doPlaySound(p, DefaultSound.DEATH.getSound());
    }

    @Override
    public void playEpic(Player p) {
        doPlaySound(p, DefaultSound.EPIC.getSound());
    }

    @Override
    public void play(Player p, Sound sound) {
        doPlaySound(p, sound);
    }

    private void doPlaySound(Player p, Sound sound) {
        CorePlayer cp = system.getCorePlayer(p);

        if (cp == null || system.getCorePlayer(p).getSettings().isPlaySounds()) {
            p.playSound(p.getLocation(), sound, 1, 1);
        }
    }

}

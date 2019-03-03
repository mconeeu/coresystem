package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import org.bukkit.entity.Player;

public class SetWorldSpawnCMD extends CorePlayerCommand {

    public SetWorldSpawnCMD() {
        super("setworldspawn", "system.bukkit.world.setspawn");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        p.performCommand("world setspawn");
        return true;
    }

}

/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

public class SpawnCMD extends CorePlayerCommand {

    private CoreWorld world;

    public SpawnCMD(CoreWorld world) {
        super("spawn");
        this.world = world;

        if (world != null) {
            if (world.getLocation("spawn") == null) {
                world.setLocation("spawn", world.bukkit().getSpawnLocation());
            }
        } else {
            CoreSystem.getInstance().sendConsoleMessage("§cSpawnCMD: The world is null, please check all world directories");
        }
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            world.teleport(p, "spawn");
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Benutze §c/spawn §4um dich zum Spawn zu teleportieren");
        }

        return true;
    }
}

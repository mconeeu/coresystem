/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

public class SpawnCMD extends CorePlayerCommand {

    private final CorePlugin plugin;
    private final CoreWorld world;
    private final int cooldown;

    public SpawnCMD(CorePlugin plugin, CoreWorld world, int cooldown) {
        super("spawn");
        this.plugin = plugin;
        this.world = world;
        this.cooldown = cooldown;

        if (world != null) {
            if (world.getLocation("spawn") == null) {
                world.setLocation("spawn", world.bukkit().getSpawnLocation());
            }
        } else {
            plugin.sendConsoleMessage("§cSpawnCMD: The world is null, please check all world directories");
        }
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            plugin.getMessenger().send(p, "§2Teleportiere zum Spawn...");
            BukkitCoreSystem.getSystem().getCorePlayer(p).teleportWithCooldown(world.getLocation("spawn"), cooldown);
        } else {
            plugin.getMessenger().send(p, "§4Benutze §c/spawn §4um dich zum Spawn zu teleportieren");
        }

        return true;
    }
}

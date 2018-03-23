/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public abstract class CoreCommand {

    @Getter
    private String command;
    @Getter
    private String permission;
    @Getter
    private String[] aliases;

    public CoreCommand(String command, String permission, String... alisases) {
        this.command = command;
        this.permission = permission;
        this.aliases = alisases;

        CoreSystem.getInstance().registerCommand(this);
    }

    public abstract void execute(CommandSender sender, String[] args);

}

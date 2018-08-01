/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.command;


import eu.mcone.coresystem.api.bukkit.CoreSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class CoreCommand implements CommandExecutor {

    private Field bukkitCommandMap;
    @Getter
    private JavaPlugin instance;
    @Getter
    private String command;
    @Getter
    private CommandExecutor commandExecutor;
    @Getter
    private Boolean unregistered = false;

    public CoreCommand(final JavaPlugin instance, final String command) {
        try {
            this.instance = instance;
            this.command = command;
            this.commandExecutor = this;

            this.bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            this.bukkitCommandMap.setAccessible(true);
            this.addCommand();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCommand() {
        this.instance.getCommand(this.command).setExecutor(this.commandExecutor);
        CoreSystem.getInstance().registerCoreCommand(this);
    }

    public void unregisterCommand() {
        try {
            if (unregistered) {
                CoreSystem.getInstance().sendConsoleMessage("§cThe command " + this.command + " has already been unregistered");
            } else {
                CommandMap commandMap = (CommandMap) this.bukkitCommandMap.get(Bukkit.getServer());
                Field knownCommands = commandMap.getClass().getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);
                Map<String, Command> cmds = (Map<String, Command>) knownCommands.get(commandMap);
                cmds.remove(this.command);
                knownCommands.set(commandMap, cmds);
                this.unregistered = true;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerCommand() {
        try {
            if (unregistered) {
                CommandMap commandMap = (CommandMap) this.bukkitCommandMap.get(Bukkit.getServer());
                Field knownCommands = commandMap.getClass().getDeclaredField("knownCommands");
                knownCommands.setAccessible(true);
                Map<String, Command> cmds = (Map<String, Command>) knownCommands.get(commandMap);
                cmds.put(this.command, this.instance.getCommand(this.command));
                knownCommands.set(commandMap, cmds);
                this.unregistered = false;
            } else {
                CoreSystem.getInstance().sendConsoleMessage("§cThe command " + this.command + " has already been registered");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

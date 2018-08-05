/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.util.CorePluginManager;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.core.util.CooldownSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.*;

public class PluginManager implements CorePluginManager {

    private static CommandMap commandMap;

    @Getter
    private CooldownSystem cooldownSystem;
    @Getter
    private List<CorePlugin> corePlugins;
    private Map<CoreCommand, CorePlugin> commands;
    private Map<CoreInventory, CorePlugin> inventories;

    static {
        try {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            f.set(Bukkit.getPluginManager(), commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PluginManager() {
        this.cooldownSystem = new CooldownSystem();
        this.corePlugins = new ArrayList<>();
        this.commands = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    public void disable() {
        corePlugins.clear();
        commands.clear();
        inventories.clear();
    }

    @Override
    public void registerCorePlugin(CorePlugin plugin) throws CoreException {
        if (!corePlugins.contains(plugin)) {
            corePlugins.add(plugin);
        } else {
            throw new CoreException("CorePlugin "+plugin.getPluginName()+" already registered in BCS!");
        }
    }

    @Override
    public void registerCoreInventory(CoreInventory inventory, CorePlugin plugin) {
        inventories.put(inventory, plugin);
    }

    @Override
    public CoreInventory getCoreInventory(String name) {
        for (CoreInventory inv : inventories.keySet()) {
            if (inv.getName().equals(name)) {
                return inv;
            }
        }

        return null;
    }

    @Override
    public Collection<CoreInventory> getCoreInventories() {
        return inventories.keySet();
    }

    @Override
    public Collection<CoreInventory> getCoreInventories(CorePlugin plugin) {
        List<CoreInventory> result = new ArrayList<>();

        for (HashMap.Entry<CoreInventory, CorePlugin> e : inventories.entrySet()) {
            if (e.getValue().equals(plugin)) {
                result.add(e.getKey());
            }
        }

        return result;
    }

    @Override
    public CoreCommand registerCoreCommand(CoreCommand command, CorePlugin plugin) {
        commands.put(command, plugin);
        commandMap.register(plugin.getDescription().getName(), command);

        return command;
    }

    @Override
    public CoreCommand getCoreCommand(String name) {
        for (CoreCommand cmd : commands.keySet()) {
            if (cmd.getName().equals(name)) {
                return cmd;
            }
        }

        return null;
    }

    @Override
    public Collection<CoreCommand> getCoreCommands() {
        return commands.keySet();
    }

    @Override
    public Collection<CoreCommand> getCoreCommands(CorePlugin plugin) {
        List<CoreCommand> result = new ArrayList<>();

        for (HashMap.Entry<CoreCommand, CorePlugin> e : commands.entrySet()) {
            if (e.getValue().equals(plugin)) {
                result.add(e.getKey());
            }
        }

        return result;
    }

}

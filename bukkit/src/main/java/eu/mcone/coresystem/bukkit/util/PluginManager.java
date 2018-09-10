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
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.*;

public class PluginManager implements CorePluginManager {

    private static CommandMap commandMap;

    @Getter
    private CooldownSystem cooldownSystem;
    @Getter
    private List<CorePlugin> corePlugins;
    private Map<CorePlugin, List<CoreCommand>> commands;
    private Map<CorePlugin, Map<Player, CoreInventory>> inventories;

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
    public void registerCoreInventory(CoreInventory inventory, Player player, CorePlugin plugin) {
        if (inventories.getOrDefault(plugin, null) != null) {
            inventories.get(plugin).put(player, inventory);
        } else {
            inventories.put(plugin, new HashMap<Player, CoreInventory>(){{
                put(player, inventory);
            }});
        }
    }

    @Override
    public CoreInventory getCoreInventory(CorePlugin plugin, String name) {
        for (CoreInventory inv : inventories.getOrDefault(plugin, Collections.emptyMap()).values()) {
            if (inv.getName().equals(name)) {
                return inv;
            }
        }
        return null;
    }

    @Override
    public Collection<CoreInventory> getCoreInventories() {
        Set<CoreInventory> inventories = new HashSet<>();

        for (Map<Player, CoreInventory> inventoryMap : this.inventories.values()) {
            inventories.addAll(inventoryMap.values());
        }

        return inventories;
    }

    @Override
    public Collection<CoreInventory> getCoreInventories(CorePlugin plugin) {
        return new ArrayList<>(inventories.getOrDefault(plugin, Collections.emptyMap()).values());
    }

    @Override
    public CoreCommand registerCoreCommand(CoreCommand command, CorePlugin plugin) {
        if (commands.getOrDefault(plugin, null) != null) {
            commands.get(plugin).add(command);
        } else {
            commands.put(plugin, new ArrayList<>(Collections.singletonList(command)));
        }

        commandMap.register(plugin.getDescription().getName(), command);
        return command;
    }

    @Override
    public CoreCommand getCoreCommand(CorePlugin plugin, String name) {
        for (CoreCommand cmd : commands.getOrDefault(plugin, Collections.emptyList())) {
            if (cmd.getName().equals(name)) {
                return cmd;
            }
        }

        return null;
    }

    @Override
    public Collection<CoreCommand> getCoreCommands() {
        Set<CoreCommand> commands = new HashSet<>();

        for (List<CoreCommand> commandList : this.commands.values()) {
            commands.addAll(commandList);
        }

        return commands;
    }

    @Override
    public Collection<CoreCommand> getCoreCommands(CorePlugin plugin) {
        return new ArrayList<>(commands.getOrDefault(plugin, Collections.emptyList()));
    }

}

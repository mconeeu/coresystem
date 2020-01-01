/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfile;
import eu.mcone.coresystem.api.bukkit.util.CorePluginManager;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import eu.mcone.coresystem.bukkit.inventory.modification.CoreInventoryModificationManager;
import eu.mcone.coresystem.core.util.CoreCooldownSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.*;

public class PluginManager implements CorePluginManager {

    private static CommandMap commandMap;

    @Getter
    private CoreCooldownSystem cooldownSystem;
    @Getter
    private List<CorePlugin> corePlugins;
    private Map<CorePlugin, List<CoreCommand>> commands;
    private Map<Player, CoreInventory> coreInventories;
    private Map<CorePlugin, InventoryModificationManager> inventoryModificationManagers;
    private List<AnvilInventory> anvilInventories;
    private List<GameProfile> gameProfiles;
    @Getter
    private String gameProfileWorld;

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
        this.cooldownSystem = new CoreCooldownSystem();
        this.corePlugins = new ArrayList<>();
        this.commands = new HashMap<>();
        this.coreInventories = new HashMap<>();
        this.inventoryModificationManagers = new HashMap<>();
        this.anvilInventories = new ArrayList<>();
        this.gameProfiles = new ArrayList<>();
        this.gameProfileWorld = Bukkit.getWorlds().get(0).getName();
    }

    public void disable() {
        corePlugins.clear();
        commands.clear();
        inventoryModificationManagers.clear();
        gameProfiles.clear();
    }

    @Override
    public void registerCorePlugin(CorePlugin plugin) throws CoreException {
        if (!corePlugins.contains(plugin)) {
            corePlugins.add(plugin);
        } else {
            throw new CoreException("CorePlugin " + plugin.getPluginName() + " already registered in BCS!");
        }
    }

    public void registerCoreInventory(Player player, CoreInventory inv) {
        coreInventories.put(player, inv);
    }

    public CoreInventory getCurrentCoreInventory(Player player) {
        return coreInventories.getOrDefault(player, null);
    }

    @Override
    public InventoryModificationManager getInventoryModificationManager(final CorePlugin plugin) {
        if (inventoryModificationManagers.containsKey(plugin)) {
            return inventoryModificationManagers.get(plugin);
        } else {
            inventoryModificationManagers.put(plugin, new CoreInventoryModificationManager(plugin));
            return inventoryModificationManagers.get(plugin);
        }
    }

    public void registerCoreAnvilInventory(AnvilInventory inventory) {
        anvilInventories.add(inventory);
    }

    @Override
    public void registerGameProfile(final GameProfile gameProfile) {
        gameProfiles.add(gameProfile);
    }

    public Collection<AnvilInventory> getCoreAnvilInventories() {
        return anvilInventories;
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

    @SuppressWarnings("unchecked")
    @Override
    public void unregisterCoreCommand(CoreCommand command) {
        try {
            Field map = SimpleCommandMap.class.getDeclaredField("knownCommands");
            map.setAccessible(true);
            Map<String, Command> commands = ((Map<String, Command>) map.get(commandMap));
            for (String alias : command.getAliases()) {
                commands.remove(alias);
            }
            map.setAccessible(false);

            for (Map.Entry<CorePlugin, List<CoreCommand>> e : this.commands.entrySet()) {
                e.getValue().remove(command);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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

    @Override
    public Collection<GameProfile> getGameProfiles() {
        return gameProfiles;
    }

    @Override
    public void setGameProfileWorld(CoreWorld world) {
        gameProfileWorld = world.getName();
    }

}

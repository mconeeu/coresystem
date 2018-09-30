/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.exception.CoreException;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CorePlugin extends JavaPlugin implements GlobalCorePlugin {

    @Getter
    private String pluginName;
    private String consolePrefix;
    @Getter
    private Messager messager;

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation) {
        this.pluginName = pluginName;
        this.consolePrefix = "§8[" + pluginColor + pluginName + "§8] §7";
        this.messager = new Messager(prefixTranslation);

        if (CoreSystem.getInstance() != null) {
            try {
                CoreSystem.getInstance().getPluginManager().registerCorePlugin(this);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendConsoleMessage(String message) {
        getServer().getConsoleSender().sendMessage(consolePrefix+message);
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void registerCommands(CoreCommand... commands) {
        for (CoreCommand command : commands) {
            CoreSystem.getInstance().getPluginManager().registerCoreCommand(command, this);
        }
    }

}

/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Transl;
import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class CoreCommand extends Command {

    private final static String NO_PERM_MESSAGE = Transl.get("system.prefix.server", CoreTranslationManager.DEFAULT_LANGUAGE) + Transl.get("system.command.noperm", CoreTranslationManager.DEFAULT_LANGUAGE);

    @Getter
    private final String permission;

    public CoreCommand(String name) {
        this(name, null);
    }

    public CoreCommand(String name, String permission, String... aliases) {
        super(name);

        this.permission = permission;
        setAliases(Arrays.asList(aliases));
        setPermissionMessage(NO_PERM_MESSAGE);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), ((Player) sender).getUniqueId()))
            return false;

        if (permission == null || sender.hasPermission(permission)) {
            return onCommand(sender, args);
        } else {
            if (sender instanceof Player)
                Msg.sendTransl(sender, "system.command.noperm");
            return false;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (permission == null || sender.hasPermission(permission)) {
            return onTabComplete(sender, args);
        } else return null;
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

}

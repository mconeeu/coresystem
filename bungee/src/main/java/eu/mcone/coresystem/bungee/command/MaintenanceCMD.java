/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class MaintenanceCMD extends CoreCommand implements TabExecutor {

    public MaintenanceCMD(){
        super("wartung", "system.bungee.maintenance");
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Msg.sendSimple(sender, "§8§m------------------------------------------");

            if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                Msg.sendSimple(sender, "§2Der Wartungsmodus ist aktiviert");
            } else {
                Msg.sendSimple(sender, "§4Der Wartungsmodus ist deaktiviert");
            }

            Msg.sendSimple(sender, "");
            Msg.sendSimple(sender, "§7Wartungsmodus aktivieren §f/wartung on");
            Msg.sendSimple(sender, "§7Wartungsmodus deaktivieren §f/wartung off");
            Msg.sendSimple(sender,  "§8§m------------------------------------------");
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (!BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference("maintenance", true);
                    Msg.send(sender, "§2Wartungsmodus aktiviert!");
                }else{
                    Msg.send(sender, "§4Der Wartungsmodus ist bereits aktiviert!");
                }
                return;
            } else if (args[0].equalsIgnoreCase("off")) {
                if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference("maintenance", false);
                    Msg.send(sender, "§2Wartungsmodus deaktiviert!");
                }else{
                    Msg.send(sender, "§4Der Wartungsmodus ist nicht aktiviert!");
                }
                return;
            }
        }

        Msg.send(sender, "§4Bitte benutze: §c/wartung [<on | off>]");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            Set<String> matches = new HashSet<>();

            if ("on".startsWith(search)) {
                matches.add("on");
            }
            if ("off".startsWith(search)) {
                matches.add("off");
            }

            return matches;
        }

        return ImmutableSet.of();
    }

}

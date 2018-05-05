/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.util.Messager;
import eu.mcone.coresystem.api.bungee.util.Preference;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class WartungCMD extends Command {

    public WartungCMD(){
        super("wartung", "system.bungee.wartung");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Messager.send(sender, "§8§m------------------------------------------");

            if (BungeeCoreSystem.getSystem().getPreferences().getBoolean(Preference.MAINTENANCE)) {
                Messager.send(sender, "§2Der Wartungsmodus ist aktiviert");
            } else {
                Messager.send(sender, "§4Der Wartungsmodus ist deaktiviert");
            }

            Messager.send(sender, "");
            Messager.send(sender, "§7Wartungsmodus aktivieren §f/wartung on");
            Messager.send(sender, "§7Wartungsmodus deaktivieren §f/wartung off");
            Messager.send(sender,  "§8§m------------------------------------------");
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (!BungeeCoreSystem.getSystem().getPreferences().getBoolean(Preference.MAINTENANCE)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference(Preference.MAINTENANCE, "true");
                    Messager.send(sender, "§2Wartungsmodus aktiviert!");
                }else{
                    Messager.send(sender, "§4Der Wartungsmodus ist bereits aktiviert!");
                }
                return;
            } else if (args[0].equalsIgnoreCase("off")) {
                if (BungeeCoreSystem.getSystem().getPreferences().getBoolean(Preference.MAINTENANCE)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference(Preference.MAINTENANCE, "false");
                    Messager.send(sender, "§2Wartungsmodus deaktiviert!");
                }else{
                    Messager.send(sender, "§4Der Wartungsmodus ist nicht aktiviert!");
                }
                return;
            }
        }

        Messager.send(sender, "§4Bitte benutze: §c/wartung [<on | off>]");
    }

}

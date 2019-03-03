/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCMD extends Command {

    public MaintenanceCMD(){
        super("wartung", "system.bungee.wartung");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            BungeeCoreSystem.getInstance().getMessager().send(sender, "§8§m------------------------------------------");

            if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Wartungsmodus ist aktiviert");
            } else {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Wartungsmodus ist deaktiviert");
            }

            BungeeCoreSystem.getInstance().getMessager().send(sender, "");
            BungeeCoreSystem.getInstance().getMessager().send(sender, "§7Wartungsmodus aktivieren §f/wartung on");
            BungeeCoreSystem.getInstance().getMessager().send(sender, "§7Wartungsmodus deaktivieren §f/wartung off");
            BungeeCoreSystem.getInstance().getMessager().send(sender,  "§8§m------------------------------------------");
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (!BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference("maintenance", true);
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Wartungsmodus aktiviert!");
                }else{
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Wartungsmodus ist bereits aktiviert!");
                }
                return;
            } else if (args[0].equalsIgnoreCase("off")) {
                if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
                    BungeeCoreSystem.getSystem().getPreferences().setPreference("maintenance", false);
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Wartungsmodus deaktiviert!");
                }else{
                    BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Wartungsmodus ist nicht aktiviert!");
                }
                return;
            }
        }

        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Bitte benutze: §c/wartung [<on | off>]");
    }

}
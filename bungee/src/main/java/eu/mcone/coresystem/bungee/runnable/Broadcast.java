/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;

public class Broadcast implements Runnable{

    private int i = 1;

    public void run(){
        i++;

        if (i == 1) {
            for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.broadcast1", p));
            }
        } else if (i == 2) {
            for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.broadcast2", p));
            }
        } else if (i == 3) {
            for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.broadcast3", p));
            }
        } else if (i == 4) {
            for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.broadcast4", p));
            }
        }else if (i == 5) {
            for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p.bungee(), BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.broadcast5", p));
            }
            i = 1;
        }
    }
}

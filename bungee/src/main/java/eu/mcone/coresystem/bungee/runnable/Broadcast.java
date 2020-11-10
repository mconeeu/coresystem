/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;

public class Broadcast implements Runnable{

    private int i = 1;

    public void run(){
        i++;

        if (i == 1) {
            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessenger().sendSimpleTransl(p.bungee(),"system.bungee.broadcast1");
            }
        } else if (i == 2) {
            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessenger().sendSimpleTransl(p.bungee(), "system.bungee.broadcast2");
            }
        } else if (i == 3) {
            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessenger().sendSimpleTransl(p.bungee(), "system.bungee.broadcast3");
            }
        } else if (i == 4) {
            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessenger().sendSimpleTransl(p.bungee(), "system.bungee.broadcast4");
            }
        }else if (i == 5) {
            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()){
                BungeeCoreSystem.getInstance().getMessenger().sendSimpleTransl(p.bungee(), "system.bungee.broadcast5");
            }
            i = 1;
        }
    }
}

/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Broadcast implements Runnable{

    private int i = 1;

    public void run(){
        i++;

        if (i == 1) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                Messager.sendSimple(all, CoreSystem.sqlconfig.getLiveConfigValue("bc1"));
            }
        } else if (i == 2) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                Messager.sendSimple(all, CoreSystem.sqlconfig.getLiveConfigValue("bc2"));
            }
        } else if (i == 3) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                Messager.sendSimple(all, CoreSystem.sqlconfig.getLiveConfigValue("bc3"));
            }
        } else if (i == 4) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                Messager.sendSimple(all, CoreSystem.sqlconfig.getLiveConfigValue("bc4"));
            }
        }else if (i == 5) {
            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                Messager.sendSimple(all, CoreSystem.sqlconfig.getLiveConfigValue("bc5"));
            }
            i = 1;
        }
    }
}

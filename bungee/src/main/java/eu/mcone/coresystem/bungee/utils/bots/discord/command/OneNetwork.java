/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils.bots.discord.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.DiscordCommand;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.Role;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class OneNetwork extends DiscordCommand {

    public OneNetwork() {
        super("netzwerk", Role.EVERYONE,"Netzwerk", "n", "network", "Network");
    }

    @Override
    public boolean onCommand(Member member, String[] args, PrivateMessageReceivedEvent e) {
        Message message = e.getMessage();

        if (args.length == 1) {
            message.getPrivateChannel().sendMessage("**MCONE** *Minecraft Netzwerk*\n" +
                    "**mcone:netzwerk info** » *Informationen über denn Aktuellen Staus aller System von MCONE.EU.*\n").queue();
        } else if (args.length == 2){
            if (args[1].equalsIgnoreCase("info")) {
                String teamSpeak;

                if (BungeeCoreSystem.getSystem().getTeamspeakVerifier().isBotRunning()) {
                    teamSpeak = "*Läuft*";
                } else {
                    teamSpeak = "*Nicht ereichbar*";
                }

                message.getPrivateChannel().sendMessage("**MCONE** *Minecraft Netzwerk*\n" +
                        "**Teamspeak-Server** » " + teamSpeak + "\n" +
                        "**Minecraft-Server** » Läuft\n").queue();
            }
        }

        return false;
    }
}

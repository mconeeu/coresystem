/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils.bots.discord.command;

import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.DiscordCommand;
import eu.mcone.coresystem.bungee.utils.bots.discord.command.utils.Role;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class Help extends DiscordCommand {

    public Help() {
        super("help", Role.EVERYONE, "h", "Help");

    }

    @Override
    public boolean onCommand(Member member, String[] args, PrivateMessageReceivedEvent e) {
        Message message = e.getMessage();

        if (args.length == 1) {
            message.getPrivateChannel().sendMessage("**mcone:help** » *Informationen über alle befehle.*\n" +
                    "**mcone:help bot** » *Informationen über den Bot.*\n" +
                    "**mcone:help verify** » *Anleitung zur Verifierung auf dem MCONE Discord server über das MCONE Minecraft Netzwerk.*\n" +
                    "**mcone:help netzwerk** » *In Arbeit*").queue();
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("bot")) {
                message.getPrivateChannel().sendMessage("Entwickelt von: *TwinsterHD*\n" +
                        "\n" +
                        "**Wir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten.**\n" +
                        "**Deshalb sind auch alle von uns verwendeten Plugins und Systeme ausschließlich selbst entwickelt!**").queue();
            } else if (args[1].equalsIgnoreCase("verify")) {
                message.getPrivateChannel().sendMessage("-----[ **MCONE** *Verifizierungs System* ]-----\n" +
                        "Um deinen Discord Account auf unserem Discord server verifizieren zu können, musst du zum einnen auf unserem Discord Server sein,\n" +
                        "und zum anderen mit deinem OneNetwork client auf unserem OneNetwork Netzwerk (play.mcone.eu) sein.\n" +
                        "wenn dies erledigt hast, musst du auf unserem Minecrtaft netzwerk den Befehl /discord link (Dein Discord Name) ausführen,\n"  +
                        "darauf hin wird dich dieser Bot anschreiben und dich um deinen OneNetwork InGame Namen fragen den du ihm senden musst.\n" +
                        "hast du deinen OneNetwork InGame Namen dem Bot gesendet ist der Verifizierungsvorgang auch schon beendet, und du bist nun auf unserem Discord Server verifizeirt.").queue();
            } else if (args[1].equalsIgnoreCase("netzwerk")) {
                message.getPrivateChannel().sendMessage("-----[ **MCONE** *Minecraft Netzwerk* ]-----\n" +
                        "*Hier sollst du zukünftig Informationen über denn Aktuellen Staus aller System von MCONE.EU bekommen.*").queue();
            }
        } else {
            message.getPrivateChannel().sendMessage("Bitte benutze: mcone:help").queue();
        }

        return false;
    }
}

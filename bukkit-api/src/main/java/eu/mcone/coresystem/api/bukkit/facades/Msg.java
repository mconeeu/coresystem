package eu.mcone.coresystem.api.bukkit.facades;

import eu.mcone.coresystem.api.bukkit.chat.Broadcast;
import eu.mcone.coresystem.api.bukkit.chat.Messenger;
import eu.mcone.coresystem.api.core.chat.CoreMsgFacade;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

public class Msg extends CoreMsgFacade {

    @Deprecated
    public static void send(CommandSender sender, String message) {
        getMessenger().send(sender, message);
    }

    @Deprecated
    public static void send(CommandSender sender, BaseComponent... baseComponents) {
        getMessenger().send(sender, baseComponents);
    }

    @Deprecated
    public static void sendSuccess(CommandSender sender, String message) {
        getMessenger().sendSuccess(sender, message);
    }

    @Deprecated
    public static void sendInfo(CommandSender sender, String message) {
        getMessenger().sendInfo(sender, message);
    }

    @Deprecated
    public static void sendWarning(CommandSender sender, String message) {
        getMessenger().sendWarning(sender, message);
    }

    @Deprecated
    public static void sendError(CommandSender sender, String message) {
        getMessenger().sendError(sender, message);
    }

    @Deprecated
    public static void send(CommandSender sender, TextLevel level, String message) {
        getMessenger().send(sender, level, message);
    }

    @Deprecated
    public static void sendSimple(CommandSender sender, String message) {
        getMessenger().sendSimple(sender, message);
    }

    @Deprecated
    public static void sendSimple(CommandSender sender, BaseComponent... baseComponents) {
        getMessenger().sendSimple(sender, baseComponents);
    }

    public static void sendTransl(CommandSender sender, String translation) {
        getMessenger().sendTransl(sender, translation);
    }

    public static void sendTransl(CommandSender sender, String translationKey, Object... replacements) {
        getMessenger().sendTransl(sender, translationKey, replacements);
    }

    public static void sendSimpleTransl(CommandSender sender, String translation) {
        getMessenger().sendSimpleTransl(sender, translation);
    }

    public static void sendSimpleTransl(CommandSender sender, String translationKey, Object... replacements) {
        getMessenger().sendSimpleTransl(sender, translationKey, replacements);
    }
    
    public static void broadcast(Broadcast broadcast) {
        getMessenger().broadcast(broadcast);
    }

    public static void broadcastSimple(Broadcast broadcast) {
        getMessenger().broadcastSimple(broadcast);
    }
    
    private static Messenger getMessenger() {
        return (Messenger) getCorePlugin().getMessenger();
    }
    
}

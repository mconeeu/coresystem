package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.CoreMessenger;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessenger extends CoreMessenger<CommandSender> implements Messenger {

    public BungeeMessenger(GlobalCoreSystem coreSystem, String prefixTranslation) {
        super(coreSystem, prefixTranslation);
    }

    @Override
    protected GlobalCorePlayer getCorePlayer(CommandSender player) {
        try {
            return (BungeeCorePlayer) BungeeCoreSystem.getSystem().getCorePlayer((ProxiedPlayer) player);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    protected void dispatchMessage(CommandSender sender, String message) {
        dispatchMessage(sender, TextComponent.fromLegacyText(message));
    }

    @Override
    protected void dispatchMessage(CommandSender sender, BaseComponent... baseComponents) {
        sender.sendMessage(baseComponents);
    }

}

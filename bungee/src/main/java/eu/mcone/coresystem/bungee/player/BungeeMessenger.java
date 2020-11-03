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

public class BungeeMessenger extends CoreMessenger<ProxiedPlayer, CommandSender> implements Messenger {

    public BungeeMessenger(GlobalCoreSystem coreSystem, String prefixTranslation) {
        super(coreSystem, prefixTranslation);
    }

    @Override
    protected void dispatchMessage(ProxiedPlayer player, String message) {
        dispatchMessage(player, TextComponent.fromLegacyText(message));
    }

    @Override
    protected void dispatchMessage(ProxiedPlayer player, BaseComponent... baseComponents) {
        player.sendMessage(baseComponents);
    }

    @Override
    protected GlobalCorePlayer getCorePlayer(ProxiedPlayer player) {
        return (BungeeCorePlayer) BungeeCoreSystem.getSystem().getCorePlayer(player);
    }

    @Override
    protected void dispatchSenderMessage(CommandSender sender, String message) {
        dispatchSenderMessage(sender, TextComponent.fromLegacyText(message));
    }

    @Override
    protected void dispatchSenderMessage(CommandSender sender, BaseComponent... baseComponents) {
        sender.sendMessage(baseComponents);
    }

}

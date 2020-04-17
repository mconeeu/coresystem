package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.util.Messenger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class BroadcastMessageEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Messenger.Broadcast broadcast;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerVanishEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CorePlayer player;
    private final boolean isVanished;
    private String cancelCause;
    @Setter
    private boolean cancelled;

    public void setCancelled(boolean cancelled, String cancelCause) {
        this.cancelled = cancelled;
        this.cancelCause = cancelCause;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
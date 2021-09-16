package eu.mcone.coresystem.api.bukkit.event.world;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class WorldDeleteEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CoreWorld world;
    private final Player player;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

package eu.mcone.coresystem.api.bukkit.event.objectiv;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreObjective;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class CoreObjectiveCreateEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CoreObjective sidebarObjective;
    private final Player player;
    @Setter
    private boolean cancelled = false;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

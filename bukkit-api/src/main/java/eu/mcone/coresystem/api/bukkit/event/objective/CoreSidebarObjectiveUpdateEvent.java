package eu.mcone.coresystem.api.bukkit.event.objective;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjectiveEntry;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjective;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class CoreSidebarObjectiveUpdateEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CoreSidebarObjective sidebarObjective;
    private final CoreSidebarObjectiveEntry coreSidebarObjectiveEntry;
    private final Player player;
    @Setter
    private boolean cancelled = false;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

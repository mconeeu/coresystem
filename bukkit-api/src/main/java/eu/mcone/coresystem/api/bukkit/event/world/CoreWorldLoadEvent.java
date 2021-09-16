package eu.mcone.coresystem.api.bukkit.event.world;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class CoreWorldLoadEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CoreWorld world;
    private final World bukkitWorld;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

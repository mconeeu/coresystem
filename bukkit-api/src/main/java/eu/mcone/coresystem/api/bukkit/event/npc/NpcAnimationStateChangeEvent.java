package eu.mcone.coresystem.api.bukkit.event.npc;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NpcAnimationStateChangeEvent extends Event {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final NPC npc;
    private final NpcAnimationState state;

    public NpcAnimationStateChangeEvent(final NPC npc, final NpcAnimationState state) {
        this.npc = npc;
        this.state = state;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum NpcAnimationState {
        START,
        END
    }
}

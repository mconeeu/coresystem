package eu.mcone.coresystem.api.bukkit.event.npc;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class NpcAnimationProgressEvent extends Event {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final NPC npc;
    private final int tick;
    private final int progress;

    public NpcAnimationProgressEvent(final NPC npc, final int tick, final int progress) {
        this.npc = npc;
        this.tick = tick;
        this.progress = progress;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}

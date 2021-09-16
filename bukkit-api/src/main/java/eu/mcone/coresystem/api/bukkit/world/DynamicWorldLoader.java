package eu.mcone.coresystem.api.bukkit.world;

import java.util.Set;

public interface DynamicWorldLoader {

    Set<CoreWorld> getBlacklist();

    boolean addToBlacklist(CoreWorld... world);

    boolean removeFromBlacklist(CoreWorld world);

    boolean isBlacklisted(CoreWorld world);
}

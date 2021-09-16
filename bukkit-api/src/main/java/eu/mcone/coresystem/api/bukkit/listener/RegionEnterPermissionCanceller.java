package eu.mcone.coresystem.api.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.world.RegionEnterEvent;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.Region;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionEnterPermissionCanceller implements Listener {

    private final CoreWorld world;
    private final Region[] regions;
    private final String permission;
    private final boolean notify;

    public RegionEnterPermissionCanceller(World world, String permission, boolean notify, String... regions) {
        this(CoreSystem.getInstance().getWorldManager().getWorld(world), permission, notify, regions);
    }

    public RegionEnterPermissionCanceller(CoreWorld world, String permission, boolean notify, String... regions) {
        this.world = world;
        this.regions = new Region[regions.length];
        for (int i = 0; i < regions.length; i++) {
            Region region = world.getRegion(regions[i]);

            if (region != null) {
                this.regions[i] = region;
            } else {
                CoreSystem.getInstance().sendConsoleMessage("§cCould not find region "+regions[i]+" for world "+world.getName()+"!");
            }
        }
        this.permission = permission;
        this.notify = notify;
    }

    @EventHandler
    public void onEnter(RegionEnterEvent e) {
        boolean notify = this.notify;

        if (e.getWorld().equals(world)) {
            for (Region region : regions) {
                if (region.equals(e.getRegion())) {
                    if (!e.getPlayer().hasPermission(permission)) {
                        e.setCancelled(true);

                        if (notify) {
                            Msg.sendError(e.getPlayer(), "Du darfst diesen Bereich nicht betreten!");
                            notify = false;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {

    }

}

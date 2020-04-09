package eu.mcone.coresystem.bukkit.npc.nms;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityPigNPC extends EntityPig {

    public EntityPigNPC(World world) {
        super(world);

        List goalB = (List) ReflectionManager.getValue(goalSelector, "b");
        List goalC = (List) ReflectionManager.getValue(goalSelector, "c");
        List targetB = (List) ReflectionManager.getValue(targetSelector, "b");
        List targetC = (List) ReflectionManager.getValue(targetSelector, "c");

        goalB.clear();
        goalC.clear();
        targetB.clear();
        targetC.clear();
    }

    //Push entity if it collides
    @Override
    public void g(double d0, double d1, double d2) {
        //do nothing since it should stay in place
    }

    public void follow(Player p) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                CoreSystem.getInstance(),
                () -> getNavigation().a(
                        p.getLocation().getX(),
                        p.getLocation().getY(),
                        p.getLocation().getZ(),
                        1.75
                ), 0, 2 * 20);
    }

}

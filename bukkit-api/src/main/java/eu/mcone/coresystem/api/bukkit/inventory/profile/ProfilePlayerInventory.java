package eu.mcone.coresystem.api.bukkit.inventory.profile;

import eu.mcone.coresystem.api.bukkit.item.Skull;
import org.bukkit.entity.Player;

public interface ProfilePlayerInventory {

    int MAX_ITEMS = 4;
    Skull GLOBE_HEAD = Skull.fromUrl("http://textures.minecraft.net/texture/9dfc8932865fd57d9d2365f1ae2d475135d746b2af15abd33ffc2a6abd36282");

    Player getTarget();

}

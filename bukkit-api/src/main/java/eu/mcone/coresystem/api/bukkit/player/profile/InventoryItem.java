package eu.mcone.coresystem.api.bukkit.player.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryItem {

    private int slot, amount;
    private Material material;
    private short durablity;
    private String displayname;
    private List<String> lore;
    private Map<String, Integer> enchantments;
    private Set<ItemFlag> itemFlags;
    boolean unbreakable;

}
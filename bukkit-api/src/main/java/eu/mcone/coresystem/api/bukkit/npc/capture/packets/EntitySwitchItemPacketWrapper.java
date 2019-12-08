package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EntitySwitchItemPacketWrapper extends PacketWrapper {

    private final Material material;
    private boolean hasEnchantment = false;

    public EntitySwitchItemPacketWrapper(final ItemStack currentItem) {
        super(EntityAction.SWITCH_ITEM);
        this.material = currentItem.getType();
        if (currentItem.getEnchantments().size() > 0) {
            hasEnchantment = true;
        }
    }

    public ItemStack getItemStack() {
        if (hasEnchantment) {
            return new ItemBuilder(material).enchantment(Enchantment.DURABILITY, 1).create();
        } else {
            return new ItemBuilder(material).create();
        }
    }
}

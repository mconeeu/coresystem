package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@BsonDiscriminator
@Getter
public class EntitySwitchItemPacketWrapper extends PacketWrapper {

    private Material material;
    private boolean hasEnchantment;

    public EntitySwitchItemPacketWrapper() {
        super(PacketType.ENTITY_ACTION);
    }

    @BsonCreator
    public EntitySwitchItemPacketWrapper(@BsonProperty("material") final Material material, @BsonProperty("hasEnchantment") final boolean hasEnchantment) {
        super(PacketType.ENTITY_ACTION);

        this.material = material;
        this.hasEnchantment = hasEnchantment;
    }

    public EntitySwitchItemPacketWrapper(final ItemStack currentItem) {
        super(PacketType.ENTITY_ACTION);

        this.material = currentItem.getType();
        this.hasEnchantment = currentItem.getEnchantments().size() > 0;
    }

    public ItemStack buildItemStack() {
        return hasEnchantment ? new ItemBuilder(material).enchantment(Enchantment.DURABILITY, 1).create() : new ItemBuilder(material).create();
    }
}

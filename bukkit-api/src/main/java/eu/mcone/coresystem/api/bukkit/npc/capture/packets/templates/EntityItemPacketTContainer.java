package eu.mcone.coresystem.api.bukkit.npc.capture.packets.templates;

import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.EntityAction;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketTyp;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketContainer;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@BsonDiscriminator
public abstract class EntityItemPacketTContainer extends PacketContainer {

    private Material material;
    private int amount;
    private String enchantments;

    public EntityItemPacketTContainer(final EntityAction entityAction, final ItemStack item) {
        super(PacketTyp.ENTITY, entityAction);
        this.material = item.getType();
        this.amount = item.getAmount();

        this.enchantments = ItemStackTypeAdapterUtils.serializeEnchantments(item.getEnchantments());
    }

    @BsonCreator
    public EntityItemPacketTContainer(@BsonProperty("entityAction") final EntityAction entityAction, @BsonProperty("material") final Material material, @BsonProperty("amount") final int amount, @BsonProperty("enchantments") final String enchantments) {
        super(PacketTyp.ENTITY, entityAction);

        this.material = material;
        this.amount = amount;
        this.enchantments = enchantments;
    }

    @BsonIgnore
    public ItemStack constructItemStack() {
        ItemBuilder itemBuilder = new ItemBuilder(material, amount);
        if (enchantments != null) {
            Map<Enchantment, Integer> enchantmentMap = ItemStackTypeAdapterUtils.getEnchantments(enchantments);
            itemBuilder.enchantments(enchantmentMap);
        }

        return itemBuilder.create();
    }
}

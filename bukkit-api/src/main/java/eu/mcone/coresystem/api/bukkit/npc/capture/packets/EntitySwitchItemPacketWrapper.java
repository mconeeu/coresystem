package eu.mcone.coresystem.api.bukkit.npc.capture.packets;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@BsonDiscriminator
@Getter
public class EntitySwitchItemPacketWrapper extends EntityItemPacketWrapperTemplate {

    public EntitySwitchItemPacketWrapper(final ItemStack item) {
        super(EntityAction.SWITCH_ITEM_IN_HAND, item);
    }

    @BsonCreator
    public EntitySwitchItemPacketWrapper(@BsonProperty("material") final Material material, @BsonProperty("amount") final int amount, @BsonProperty("enchantments") final String enchantments) {
        super(EntityAction.SWITCH_ITEM_IN_HAND, material, amount, enchantments);
    }
}

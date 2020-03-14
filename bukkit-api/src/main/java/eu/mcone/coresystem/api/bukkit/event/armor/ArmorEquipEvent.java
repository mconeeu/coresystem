package eu.mcone.coresystem.api.bukkit.event.armor;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();
    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorType type;
    private ItemStack oldArmorPiece, newArmorPiece;

    /**
     * @param player        The player who put on / removed the armor.
     * @param type          The ArmorType of the armor added
     * @param oldArmorPiece The ItemStack of the armor removed.
     * @param newArmorPiece The ItemStack of the armor added.
     */
    public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece) {
        super(player);
        this.equipType = equipType;
        this.type = type;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled.
     */
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    public final void setOldArmorPiece(final ItemStack oldArmorPiece) {
        this.oldArmorPiece = oldArmorPiece;
    }


    public final void setNewArmorPiece(final ItemStack newArmorPiece) {
        this.newArmorPiece = newArmorPiece;
    }

    public enum EquipMethod {// These have got to be the worst documentations ever.
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        DRAG,
        /**
         * When you manually equip or unequip the item. Use to be DRAG
         */
        PICK_DROP,
        /**
         * When you right click an armor piece in the hotbar without the inventory open to equip.
         */
        HOTBAR,
        /**
         * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * When you die causing all armor to unequip
         */
        DEATH,
        ;
    }
}

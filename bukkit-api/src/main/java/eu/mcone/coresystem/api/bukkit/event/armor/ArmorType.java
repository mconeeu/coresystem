package eu.mcone.coresystem.api.bukkit.event.armor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
    HELMET(1), CHESTPLATE(2), LEGGINGS(3), BOOTS(4);

    private final int slot;

    ArmorType(int slot) {
        this.slot = slot;
    }

    /**
     * Attempts to match the ArmorType for the specified ItemStack.
     *
     * @param itemStack The ItemStack to parse the type of.
     * @return The parsed ArmorType, or null if not found.
     */
    public static ArmorType matchType(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR))
            return null;

        String type = itemStack.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_helmet") || type.endsWith("_SKULL")) return HELMET;
        else if (type.endsWith("_CHESTPLATE") || type.endsWith("_chestplate") || type.endsWith("ELYTRA")) return CHESTPLATE;
        else if (type.endsWith("_LEGGINGS") || type.endsWith("_leggings")) return LEGGINGS;
        else if (type.endsWith("_BOOTS") || type.endsWith("_boots")) return BOOTS;
        else return null;
    }

    public int getSlot() {
        return slot;
    }
}

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestInv extends CategoryInventory {

    private static final Map<Gamemode, ItemStack> ITEMS = new HashMap<>();
    static {
        for (Gamemode gamemode : Gamemode.values()) {
            ITEMS.put(gamemode, new ItemBuilder(gamemode.getItem()).displayName(gamemode.getLabel()).create());
        }
    }

    private final Gamemode gamemode;
    private TestInv(Gamemode gamemode, Player player) {
        super(gamemode.getLabel(), player, ITEMS.get(gamemode));
        this.gamemode = gamemode;

        for (Gamemode mode : Gamemode.values()) {
            addCategory(ITEMS.get(mode));
        }

        openInventory();
    }

    @Override
    protected void openCategoryInventory(ItemStack itemStack, Player player) {
        new TestInv(Gamemode.getGamemodeByMaterial(itemStack.getType()), player);
    }

    @Override
    public int setPaginatedItems(int skip, int limit, List<CategoryInventory.CategoryInvItem> items) {
        List<ItemStack> itemss = getItems(gamemode);

        for (int i = skip; i < limit; i++) {
            items.add(new CategoryInvItem(
                    itemss.get(i), null
            ));
        }

        return MAX_PAGE_SIZE * 2;
    }

    public static void openInventory(Player p) {
        openInventory(p, Gamemode.values()[0]);
    }

    public static void openInventory(Player player, Gamemode gamemode) {
        new TestInv(gamemode, player);
    }

    private static List<ItemStack> getItems(Gamemode gamemode) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < (MAX_PAGE_SIZE * 2); i++) {
            items.add(i < 18 ? new ItemBuilder(gamemode.getItem()).create() : new ItemBuilder(gamemode.getItem()).displayName(gamemode.getLabel()).create());
        }

        return items;
    }

}

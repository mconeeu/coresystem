With the CoreInventory class you can easily and quickly create an inventory.
To create a CoreInventory your class must extend the CoreInventory class.

**Create an Inventory.**
```java
public class Inventory extends CoreInventory {

 public Inventory(Player player) {
     super("Test-Inventory", //The name of the Inventory.
            player, //The player how opened the inventory.
            InventorySlot.ROW_1, //The size of the Inventory.
            InventoryOption.FILL_EMPTY_SLOTS //The Inventory options (This option fills all empty slots with a place holder)
    );

    setItem(InventorySlot.ROW_1_SLOT_5, //The slot of the item
            new ItemBuilder(Material.FEATHER, 1).create() //The display item as ItemStack
    );
    
    openInventory(); //Opens the Inventory for the Player.
 }
}
```

**Create an Inventory with an event listener in it.**
```java
public class Inventory extends CoreInventory {

 public Inventory(Player player) {
     super("Test-Inventory", //The name of the Inventory.
            player, //The player how opened the inventory.
            InventorySlot.ROW_1, //The size of the Inventory.
            InventoryOption.FILL_EMPTY_SLOTS //The Inventory options (This option fills all empty slots with a place holder)
    );

    setItem(InventorySlot.ROW_1_SLOT_5, //The slot of the item
            new ItemBuilder(Material.FEATHER, 1).create(), //The display item as ItemStack
            e -> {
                player.sendMessage("TEST MESSAGE"); //Get called when the player clicks the item.
            }       
    );
    
    openInventory(); //Opens the Inventory for the Player.
 }
}
```
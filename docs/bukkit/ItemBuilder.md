With the ItemBuilder you can create your own ItemStack or manipulate already created ones.

**Create a basic ItemStack**
```java
new ItemBuilder(
        Material.DIRT, //The Material.
        1 //The amount of the itemStack.
).create(); //This method returns teh finished item stack.
```

**Create an extended ItemStack**
```java
ItemStack item = new ItemBuilder(Material.DIRT,1)
        .displayName("§cDirtKnock")
        .lore("Dirt with Knockback", "and hidden unbreakable")
        .enchantment(Enchantment.KNOCKBACK, 1)
        .unbreakable(true)
        .itemFlags(ItemFlag.HIDE_UNBREAKABLE)
        .create(); //This method returns teh finished item stack.
```

**Modifying an ItemStack**
```java
ItemStack item = ItemBuilder.wrap(new ItemStack(Material.AIR, 1))
        .displayName("§bAIR")
        .create();
```
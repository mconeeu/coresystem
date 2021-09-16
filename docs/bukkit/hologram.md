With the Hologram Manager you can easily create holograms. 
All holograms must be created using the Hologram Manager.

**Create a basic Hologram**
```java
//This hologram can be see by all players on the server.
CoreSystem.getInstance().getHologramManager().addHologram(
        new HologramData(
                "test_holo", //The name of the Hologram
                new String[] {
                        "Line-1", //The text lines that gets displayed.
                        "Line-2"
                },
                new CoreLocation() //The location where holo gets spawned.
        )
);
```

**Create a extended Hologram**
```java
//Only selected players can see this hologram
CoreSystem.getInstance().getHologramManager().addHologram(
        new HologramData(
                "test_holo", //The name of the Hologram
                new String[] {
                        "Line-1", //The text lines that gets displayed.
                        "Line-2"
                },
                new CoreLocation() //The location where holo gets spawned.
        ),
        ListMode.BLACKLIST,
        Bukkit.getPlayer("DieserDominik"), //Only this players can see the Hologram.
        Bukkit.getPlayer("rufi")
);
```
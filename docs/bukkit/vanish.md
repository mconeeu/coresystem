With the VanishSystem you have the ability to vanish a player.
If a player is in the vanish mode no other Player can see this Player. 

**How can I vanish/un vanish a Player?**
```java
CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer("DieserDominik");
corePlayer.setVanished(true); //The player is now vanished.
corePlayer.setVanished(false); //The player is no un vanished.
```

Every vanish or un vanish the PlayerVanishEvent gets called. 
This Event contains the CorePlayer, and a boolean if the player is vanished.



If the player is longer than 5 minutes afk and doesn't move the `AfkEvent` gets called.
This Event contains the PLayer and the State of the Player (Online, Offline, Afk, Banned)

**How can I check if the player is AFK?**
```java
CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer("DieserDominik");
corePlayer.getAfkTime(); //Returns TRUE if the player is AFK
```

**How can I get the AFK time?**
```java
CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer("DieserDominik");
corePlayer.getAfkTime(); //Returns the afk time as long
```
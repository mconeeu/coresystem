With the ActionBar Api you can send one or more players a message above the action bar.

**Create a message above the action bar**
```java
CoreSystem.getInstance().createActionBar()
        .message("Test Message")
        .send(Bukkit.getPlayer("DieserDominik"));
```
With the Title api you can easily create titles and send them to a player.

**Create a basic Title**
```java
CoreSystem.getInstance().createTitle()
        .title("Test Title")
        .send(Bukkit.getPlayer("DieserDominik"));
```

**Create an extended Title**
```java
CoreSystem.getInstance().createTitle()
        .title("Test Title")
        .subTitle("Test Sub Title")
        .fadeIn(10) //Sets a fade in in seconds
        .fadeOut(5) //Sets a fade out in seconds
        .send(Bukkit.getPlayer("DieserDominik"));
```
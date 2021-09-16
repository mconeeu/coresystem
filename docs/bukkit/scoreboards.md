# Scoreboards

The following Classes helps you to create scoreboards simpler and with less code.

## CoreScoreboard

```java
public final class MyScoreboard extends CoreScoreboard {

    @Override
    public void modifyTeam(CorePlayer owner, CorePlayer player, CoreScoreboardEntry team) {
        Group g = player.isNicked() ? player.getNick().getGroup() : player.getMainGroup();
        team.priority(g.getScore()).prefix(g.getPrefix());

        if (player.isVanished()) {
            team.suffix(" §3§lⓋ");
        }
    }

}
```

Using the abstract class CoreScoreboard you can create a Tablist design 
with custom prefixes, suffixes and priorities using one simple method.   

A seperate `MyScoreboard` class will be created for every player the scoreboard will be set.
the `modifyTeam` method will be called for every player in **one players Tablist**

The `owner` object is the player that owns the tablist. 
The `player` object is one of all players that is shown on the owner tablist.

You can modify the prefix, suffix and the priority of the `player` using the team object.
The code example is real code used in the Coresystem for the default scoreboard (like in the Lobby or Community).

Set the Scoreboard via:
```java
CorePlayer cp = CoreSystem.getInstance().getCorePlayer("name");

MyScoreboard scoreboard = new MyScoreboard();
cp.setScoreboard(scoreboard);

//Sets the default scoreboard:
cp.setScoreabord(new MainTablist())
```

> **Be Careful!**   
> Setting a new Scoreboard results in removing all Objective scoreboards, as all objectives are registered in the Scoreboard!
> If you dont want all previously added objectives to be removed, store them before set and add them again after:
> ```java
> CoreObjective sidebarObjective = cp.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
> 
> cp.setScoreboard(scoreboard);
> cp.getScoreboard().setNewObjective(sidebarObjective);
> ```

The Scoreboard is automatically reloaded if a player joins or leaves (to update prefixes).
If you like to reload the scoreboard manually (i.e. if you want to update som custom suffixes) than use the following code:
```java
cp.getScoreboard().reload()
```

### `bukkit()`
returns the bukkit scoreboard class. No action required here. All Teams are automatically registered and removed.

### `getObjective(DisplaySlot slot)`
returns the objective scoreboard at the specified slot. Returns null if there was nothing set.

## CoreSidebarObjective

```java
public class MyObjective extends CoreSidebarObjective {

    public OneHitObjective() {
        super("Lobby-OneHit");
    }

    @Override
    public void onRegister(CorePlayer player) {
        setDisplayName("§7§l⚔ §f§lMyPlugin");

        setScore(8, "");
        setScore(7, "§8» §7KillStreak:");
        setScore(6, " §f" + player.bukkit().getLevel());
        setScore(5, "");
        setScore(4, "§8» §7Spieler:");
        setScore(3, " §f" + Bukkit.getOnlinePlayers().size());
        setScore(2, "");
        setScore(1, "§8»§7 Teamspeak:");
        setScore(0, " §f§ots.mcone.eu");
    }

    @Override
    public void onReload(CorePlayer player) {
        setScore(6, " §f" + player.bukkit().getLevel());
        setScore(3, " §f" + Bukkit.getOnlinePlayers().size());
    }

}
```

Using the CoreSidebarObjective abstract class you can create a sidebar objective with one method.

Register the ObjectiveScoreboard vie:
```java
CorePlayer cp = Coresystem.getInstance().getCorePlayer("name");

MyObjective objective = new MyObjective();
cp.getScoreboard().setNewObjective(objective);
```

If you like to update some values on demand use the `onReload`. Use the following code to reload:
```java
CorePlayer cp = Coresystem.getInstance().getCorePlayer("name");
cp.getScoreboard().getObjective(DisplaySlot.SIDEBAR).reload();
```
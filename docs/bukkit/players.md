# CorePlayer

You can use the CorePlayer objects to retrieve data from online or offline players on MC ONE.
Use the following code to get them:

## Getting a CorePlayer instance

Get CorePlayer from bukkit player:
```java
Player p = e.getPlayer();
CorePlayer cp = Coresystem.getInstance().getCorePlayer(p);
```

Get CorePlayer by name or uuid:
```java
CorePlayer cpByName = Coresystem.getInstance().getCorePlayer("test");
CorePlayer cpByUuid = Coresystem.getInstance().getCorePlayer(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0"));
```

These methods will return an instance of `eu.mcone.bukkit.api.player.CorePlayer` 
and will throw an exception if a player with that name or uuid is not online.

If you like to get data about a player from which you are unsure if he is currently online use the following:
> Please dont use this method for players that has to be online as it will 
> have an impact on the performance whether you use online or offline players.
```java
OfflineCorePlayer ocp = Coresystem.getInstance().getOfflineCorePlayer("name");
OfflineCorePlayer ocp = Coresystem.getInstance().getOfflineCorePlayer(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0"));
```

## Methods available in offline and online player
The following methods are available for online and offline players
### `getName()`
Will return the name of the player. 
> This is not trivial! The name of the bukkit player can change if he is nicked!.
> If you like to get the real name even if the player is nicked use this instead of the bukkit player `p.getName()` method.

### `getUuid()`
Is an exact clone of the bukkit players method `p.getUniqueId()`

### `hasPermission(String permission)`
Checks if the player has a permission with checking for wildcard permissions like `system.*`
The bukkit player `p.hasPermission()` method does the exact same.

### `getPermissions()`
Returns a list of strings containing all permissions the player has. 
> Please use `hasPermission("your.permission")` to check if the player has a permission

### `getMainGroup()`
Returns the Main group of the player.   
Note that a user can have multiple groups! The main group is always the highest rank in the order.

### `getGroups()`
Returns all permission groups the player has. Yes, a player can has multiple permission groups.
> If you want to get the main group use `getMainGroup()`

### `getOnlineTime()`
Returns the online time in seconds.

### `getCoins()`
Returns the users current coins amount.
[More Infos about coins][wiki-economy]

### `getFormattedCoins()`
Returns the coins amount formatted as string with dots.

### `setCoins(amount)`
Sets the coins amount

### `addCoins(amount)`
Adds coins

### `removeCoins(amount)`
Removes coins

### `getEmeralds()`
Returns the users current emeralds amount
[More Infos about emeralds][wiki-economy]

### `getFormattedEmeralds`
Returns the emeralds amount formatted as string with dots.

### `setEmeralds`
Sets the coins amount

### `addEmeralds(amount)`
Adds emeralds

### `removeEmeralds(amount)`
Removes emeralds

### `updateGroupsFromDatabase()`
Retrieves and returns the group array live from database

### `getSettings()`
Returns the players settings:

| setting | value | description |
| --- | --- | --------|
| enableFriendRequests | boolean | allow friend requests from anyone |
| autoNick | boolean | autoNick on join and server change |
| language | [Language][javadoc-language] | language for all messages in the [translation manager](translations) |
| privateMessages | [Sender][javadoc-sender] | who is allowed to send private messages |
| privateMessages | [Sender][javadoc-sender] | who is allowed to send party invites |
| receiveIncomingReports | boolean | if responsible team members should receive report notifications |

### `getState()`
returns the [PlayerState][javadoc-playerstate]

### `getTrust()`
get the trusted user data.   
*players can report other players. If their reports are valid they get ranked higher at notifications for supporters and mods.*

| field | value | description |
| --- | --- | --- |
| getCorrectReports | int | correct reports |
| getWrongReports | int | wrong reports |
| getTrustedGroup | [TrustGroup][javadoc-trustgroup] | trusted group |

### `getSkin()`
returns an [SkinInfo][javadoc-skininfo] object

| field | value | description |
| --- | --- | --- |
| name | String | name of the player, the database skin |
| value | String | the base64 encoded json value containing the mojang skin data |
| signature | String | the hashed value by mojang to verify the integrity of the skin |
| skinType | [SkinType][javadoc-skintype] | the source of the skin (either from database, from a real player, or custom made |

## Methods only available in online player
The following methods are only available for players that are online

### `bukkit()`
returns the bukkit Player instance

### `getNick()`
returns the current nick of the player is he is nicked. null otherwise

| field | value | description |
| --- | --- | --- |
| name | String | the nickname |
| group | [Group][javadoc-group] | the nick group (either Spieler or Premium) |
| skinInfo | [SkinInfo][javadoc-skininfo] | the nicked skin |
| coins | int | the nick coins (not showing the real coins of the player to others) |
| onlinetime | long | the nick onlinetime (not showing the real onlinetime of the player) |

### `getScoreboard()`
returns an `eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard` instance. 
Defaults to [MainScoreboard][javadoc-mainscoreboard]

Learn more about [scoreboards](./scoreboards).

### `setScoreboard()`
here you can set a CoreScoreboard instance.
Learn more about [CoreScoreboards](./scoreboards)

### `getWorld()`
returns the current CoreWorld the player is in.
Learn more about [core worlds](./worlds).

### `getState(Gamemode gamemode)`
returns a [Stats][javadoc-stats] object for a specific gamemode 

### `teleportWithCooldown(Location location, int cooldown)`
teleports the player to the given location after a specific cooldown.
If the player moves during the countdown, the teleport process will be aborted.
Useful for situations where the player should not escape from fighting with other players.

### `isAfk()`
returns true if the player is marked afk by the coresystem.
Learn more about the [AFK-System](./afk).

### `isVanished()`
returnes true if the player is vanished
Learn more about the [Vanish-System](./vanish)

### `setVanished(boolean vanish)`
vanishes or unvanishes the player. 
Team members with the permission `system.bukkit.vanish.see` can see those players anyway.

[wiki-economy]: https://wiki.onegaming.group/coresystem/economy

[javadoc-language]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/translation/Language.html
[javadoc-sender]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/player/PlayerSettings.Sender.html
[javadoc-playerstate]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/player/PlayerState.html
[javadoc-trustgroup]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/overwatch/trust/TrustGroup.html
[javadoc-skininfo]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/player/SkinInfo.html
[javadoc-skintype]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/player/SkinInfo.SkinType.html
[javadoc-group]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/core/player/Group.html
[javadoc-mainscoreboard]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/bukkit/scoreboard/MainScoreboard.html
[javadoc-stats]: https://systems.gitlab.onegaming.group/coresystem/eu/mcone/coresystem/api/bukkit/player/Stats.html
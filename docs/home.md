# CoreSystem Wiki

*The whole Javadoc of all Coresystem APIs can be found [here](https://systems.gitlab.onegaming.group/coresystem/).*

This project contains Commands, Events, Managers, Helper classes and APIs to make your plugin development easier.
It contains all basic stuff like item stack helper, scoreboards, npcs, ...   
All game related APIs can be found in the [game-api project](https://gitlab.onemgaming.group/systems/gameapi).

This project includes core code for bukkit and bungee servers.   

Include in your project with maven:
```xml
<dependency> <!-- MCONE-CoreSystem bukkit -->
    <groupId>eu.mcone.coresystem</groupId>
    <artifactId>bukkit-api</artifactId>
    <version>10.9.3-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
Dont forget the MC ONE repository:
```xml
<repository>
    <id>onegaming-gitlab-systems</id>
    <url>https://gitlab.onegaming.group/api/v4/groups/systems/-/packages/maven</url>
</repository>
```



# [`Bukkit-API`](./bukkit/home)

Find here an overview of all features of the [Bukkit-API](./bukkit/home)

# `Bungee-API`

A bungee API documentation is currently not available here.   
There is no need for an additional BungeeCord plugin beside the Bungee Coresystem. 
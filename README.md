# MCONE-CoreSystem

### Description
This projects acts as default or *core* plugin for every bungee and bukkit server on the mcone network.
It contains built in features and APIs for default commands, inventories, scoreboard, npcs, holograms,
player data, packets, punish/reports, worlds, items, sounds, labymod, friends, party, and much more.

**View more features explained in the [Wiki](https://gitlab.onegaming.group/systems/coresystem/-/wikis).**  
*Javadocs can be found [here](https://systems.gitlab.onegaming.group/coresystem).*

### Build and Compile

Use `mvn install` to compile a server ready plugin jar.  
Please utilize the database credentials JVM options like here:  
`-DHost=$DB_HOST -DPort=$DB_PORT -DUsername=$DB_USERNAME -DPassword=$DB_PASSWORD`

### Coding conventions
Please familiarize yourself with the [Oracle java code conventions](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf).
All source code in this repository must be formatted as described there.

##### Bukkit specific code conventions
*Replace `***` with the function name of the class in CamelCase*
* Variable names for the following classes and their child classes must be named as specified: `Player p`, `CorePlayer cp`, `GamePlayer gp`, `Event e`.
* The plugin must have a Main class having the name of the plugin in CamelCase.
* The Main class must extend `eu.mcone.coresystem.api.bukkit.CorePlugin` and must only contain 
command & listener registrations, manager initializations, and player management.
* All listener classes must be named `***Listener` and must be put in a `listener` package which is located in the main class' package.
* All command classes must be named `***CMD` and must be put in a `command` package which is located in the main class' package.
* All inventory classes, like CoreInventories must be named `***Inventory` 
and must be put in a `inventory` package that is located in the main class' package. 
* All scoreboard classes must be named `***Scoreboard`/`***Tablist`/`***Objective` 
and must be put in a `scoreboard` package that is located in the main class' package.
* All other classes must not be placed in the package where the main class is *(The plugin package must only contain the main class)*.
They must be placed in custom named packages.

### Versioning Conventions
We *dont* utilize Semantic Versioning here, as it my not be necessary to increase the version on every commit.
Therefore we added the **Bugfix** Versioning. The rest partly depends on [Semantic Versioning](https://semver.org).

* The Version syntax is of 3 numbers seperated by dots and a `-SNAPSHOT` behind them (i.e. `0.0.1-SNAPSHOT`).
* If the plugin is still in creation process use `0.` at the beginning. Otherwise the first number must be greater than 0. 
* You can increase the version of your project if there are some small or big changes. 
Please change the maven project version in all `pom.xml` files of your repository. 
*(You can use `Ctrl+Shift+F` in IntelliJ with the file filter `pom.xml` to replace all old versions with the new one)*
* [**Bugfix**] If you just changed one or two classes in course of a small bugfix 
and you only changed a few lines of code, you dont necessarily have to increase the version.
* [**Patch**] If your project does not have an API or you just changed some code that dont modified the API, 
then increase only the last number (i.e. from `1.0.0-SNAPSHOT` to `1.0.1-SNAPSHOT`).
* [**Minor**] If the API code was changed or a bigger amount of code was changed, increase the second number
(i.e. from `1.0.1-SNAPSHOT` to `1.1.0-SNAPSHOT`)
* [**Major**] If the API code was changed and a breaking major new feature was added, increase the second number
(i.e. from `1.1.0-SNAPSHOT` to `2.0.0-SNAPSHOT`)
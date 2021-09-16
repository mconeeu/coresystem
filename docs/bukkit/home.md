# Bukkit-API

The bukkit API plugin is part on every server on MC ONE. 
Therefore, you can use every API method listed under here in every plugin on every server.

Please note that this API contains code needed on all servers. 
All game related stuff like team management or kit APIs are located in the systems/gameapi>.


### [CorePlayer](./players)

The key feature of the coresystem is the CorePlayer class. From here you can get all data about all offline and online players on MCONE.


### [Translations](./translations)

Our network is made in europe and should be used in europe. Therefore all messages send to players should utilize the Translation manager.
So there is a chance to translate all of these easy in the future, without changing them manually in the code.


### [Messenger](./messenger)

Use the Messenger to send messages or broadcast to players easily with or without prefixes,


### [Scoreboards](./scoreboards)

The coresystem allows you to create Tab and Objective scoreboards easier with extending helper classes 
located in `eu.mcone.coresystem.api.bukkit.scoreboard`.


### [NPCs](./npc/home)

Using the coresystems NpcManager you can easily spawn entities, show & hide them for specific players 
and let them do animations, effects, ...

*Please note that for spawning Armorstands, which are intended to act as text lines the [Hologram-API](#holos) should be used*


### [Overwatch-System](./overwatch/home)

The Overwatch System contains the bundels the following features: 
**Punishments**, **Reports**, **Replay-System**, **Anti-Bot**, **Anti-Cheat**.
#NPC Manager
The NPC manager can spawn all types of entities (at least soon :)).
You can also modify any available attributes of them with the use of the npc system Entity classes.

> **Please note**:  
> Normally all NPCs for a server will be created on the build server using the /npc command.
> Using the /npc command all npc data will be saved to the worlds core-config.yml
> The following methods of the NpcManager will only spawn temporary NPCs that will not be saved persistent in a world config
> and will be removed on server reload or stop.

Get an NpcManager instance:
```java
NpcManager npcManager = CoreSystem.getinstance().getNpcManager();
```

## Create NPC

```java 
//At first create the NPC Data.
NpcData npcData = new NpcData(
                     EntityType.PLAYER, //EntityType
                     "TEST", //Name
                     "§cTest", //DisplayName
                     new CoreLocation(0, 0, 0), //Location
                     new PlayerNpcData(
                            "test", //SkinName
                            "", //TablistName
                            SkinInfo.SkinType.PLAYER, //SkinType
                            alse, //Visible on Tab
                            false, //Sleeping
                            false, //Sleep with Bed
                            null //Equipment (Map<EquipmentPosition, ItemStack>)
                    )
);

//Then add the PlayerNPC temporary to the Server.
CoreSystem.getInstance().getNpcManager().addNPC(npcData);
```
Use the NpcData class to specify the attributes of an NPC. 
For any available EntityType in the coresystem exists a specified NpcData class that can be added to customize further attributes.
In this example the `PlayerNpcData` class where you can set i.e. a skin name.

#### Currently available EntityType NPC classes:
*These pages contains information about the entity specific Npc Data also!*
* [**Generic Default NPC**](./entities/generic) *(All NPC Entity classes extends this class. Have a look at here definitively)*
* [**Player**](./entities/player)
* [**Pig**](./entities/pig)

## Modify NPCs after spawned

You can modify already spawned NPCs using their specific NPC class. 
This works for temporarily spawned npc via plugin or for npcs from world configs.
Use the following code to get an NPC instance:

```java
CoreWorld world = CoreSystem.getInstance().getWorldManager().getWorld("MyWorld");
NPC npc = world.getNpc("name");
Npc npcViaNpcManager = CoreSystem.getInstance().getNpcManager().getNpc(world, "name");
```

You may get the Npc instance via the world directly or via the NpcManager. These methods will result equally.
Note: The class you got is an interface that is not holding entity specific data or methods. 
To get the child class simply cast the npc object you got before:

```java
PlayerNpc playerNpc = (PlayerNpc) npc;
```

## NpcManager methods

### `reload()`
reloads all NPCs and Data from the core-config.

### `addNPC(NpcData data)`
adds a temporary NPC to the Server. 
This NPC will not be saved in the core-config and stays until server reload|restart or NPC-Manager reload
This method returns a NPC object.

### `addNPC(NpcData data, ListMode listMode, Player... players)`
adds a temporary NPC to the Server. 
This NPC will not be saved in the core-config and stays until server reload|restart or NPC-Manager reload
use /npc or the core-config.json to add Holograms permanently. 
You can set a ListMode, this regulates with player/s can see the NPC (i.e. BLACKLIST with no players means that all players can see the NPC).

### `removeNPC(NPC npc)`
removes an existing NPC (if its an permanent NPC from core-config this is just temporary).
if you want to delete NPC from core-config permanently use ingame command /npc remove.

### `getNPC(CoreWorld world, String name)`
returns an existing NPC from the given world with the given config-name (not displayname!)
null if no NPC with this name in the world exists.

### `getNPC(int entityID)`
returns an existing NPC with a specific entity id null if no NPC with this entity id exists.

### `getNpcs()`
returns a collection of all registered NPCs.
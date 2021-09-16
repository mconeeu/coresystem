# Generic Default NPC
For each NPC type there is also an associated entity class that contains certain methods that only this one entity has. 
All these specific entity classes extend the class NPC as a base which contains the following methods.

## Generic NpcData
`eu.mcone.coreystem.api.bukkit.npc.NpcData`  
This is the main NpcData class, where you can insert the attributes that all NPC types has.
Using the `entityData` JsonElement you can specify attributes for specific entities.
> Note that using the constructor `new NpcData(...)` 
> you can insert a real object of the specific entities data class instead of a JsonElement.
> An example of how you create Npc can be found [here](../#create-npc)

|  variable         | typ           | description | 
| :---              | :---          | :---          
| type              | EntityType    | the entity type of the NPC: currently supported types are PLAYER 
| name              | String        | the config name of the NPC (must be unique per world)   
| displayName       | String        | the displayname of the NPC (must not be longer than 16 chars, including color code chars)    
| location          | CoreLocation  | the Location where the NPC should appear (including yaw & pitch)
| entityData        | JsonElement   | the entity specific Data (i.e. for PLAYER: PlayerNpcData.class) 

### `changeDisplayname(String displayname, Player... players)`
Changes the DisplayName of the Entity for the given Players.
Only the given Players will se this DisplayName, if the give players are null the change will send to all currently Online Players.

### `canBeSeenBy(Player player)`
Checks if the given Player can see the NPC.

### `getVisiblePlayersList()`
Returns a Set of all Players that can see the NPC.

### `sendState(NpcState state, Player... players)`
Send a State to the NPC.

|  State                        | ID    |
| :---                          | :---  |
| MINECART_RESET_SPAWN_TIMER    |1      |
| LIVING_ENTITY_HURT            |2      |
| LIVING_ENTITY_DEAD            |3      |
| IRON_GOLEM_THROW_HANDS_UP     |4      |
| TAMABLE_TAMING                |6      |
| TAMABLE_TAMED                 |7      |
| WOLF_SHAKE_OFF_WATER          |8      |
| EATING_ACCEPTED               |9      |
| SHEEP_EAT_GRASS               |10     |
| TNT_PLAY_IGNITE               |10     |
| IRON_GOLEM_HAND_OVER_ROSE     |11     |
| VILLAGER_MATING_HEARTS        |12     |
| VILLAGER_ANGRY                |13     |
| VILLAGER_HAPPY                |14     |
| WITCH_MAGIC                   |15     |
| ZOMBIE_TO_VILLAGER_SOUND      |16     |
| FIREWORK_EXPLODE              |17     |
| ANIMALS_IN_LOVE_HEARTS        |18     |
| SQUID_RESET_ROTATION          |19     |
| SPAWN_EXPLOSION               |20     |
| GUARDIAN_SOUND                |21     |
| PLAYER_ENABLE_REDUCED_DEBUG   |22     |
| PLAYER_DISABLE_REDUCED_DEBUG  |23     |

### `sendAnimation(NpcAnimation animation, Player... players)`
send the swing arms animation to the NPC.

### `teleport(Location loc, Player... players)`
teleport's the NPC to the given location.

### `teleport(CoreLocation location, Player... players)`
teleport's the NPC to the given location.
This method will throw an **IllegalArgumentException** if the world of the location isn't the same with the world from the NPC.

### `throwProjectile(EntityProjectile type)`
throws a projectile from the NPCs location.
This method will throw an **IllegalArgumentException** if the world of the location isn't the same with the world from the NPC.

### `throwProjectile(EntityProjectile type, Vector vector)`
throws a projectile from the NPCs location with the given vector.

### `getVector()`
returns the current Vector from the NPC as Bukkit Vector.

### `sendPackets(Packet<?>... packets)`
sends given packets to the NPC.
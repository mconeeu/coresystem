# Player NPC
This class extends the class NPC as base.

## PlayerNpcData
`eu.mcone.coreystem.api.bukkit.npc.data.PlayerNpcData`  
Use the following reference to the PlayerNpcData to create an Player NPC.
Mor information about how NPCs can be spawned [here](../#create-npc).

|  variable         | typ                  | description | 
| :---              | :---                 | :---          
| skinName          | String               | The SkinName of the NPC
| tablistName       | String               | The TablistName of the NPC 
| skinType          | SkinInfo.SkinType    | The SkinType of the NPC (Database, Player, Custom)
| visibleOnTab      | boolean              | If the player is visible in the Tablist
| sleeping          | boolean              | If the player are sleeping
| sleepWithBed      | boolean              | If the player is sleeping in an bed
| equipment         | Map                  | Sets the equipment of the Player

## PlayerNpc Methods

### `getUuid()`
returns the current UUID of the NPC as UUID object.
they UUID may change when changing name or skin of the NPC

### `getMotionPlayer()`
this method returns the MotionPlayer only an MotionCapture is set for this NPC.

### `setEquipment(EquipmentPosition position, ItemStack item, Player... players)`
Sets a specific item in the NPCs inventory and makes it visible for other players.
(if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)

### `clearEquipment()`
Clears the Equipment content of the player.

### `setSkin(SkinInfo skin, Player... players)`
Sends a packet to update the skin (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)

### `getSkin()`
returns the current skin of the NPC as SkinInfo object.

### `setSleeping(boolean sleepWithoutBed)`
set the NPC sleeping for all players on the same location.
sleepWithoutBed if true the NPC lays on the ground, otherwise he gets ported higher.

### `setAwake()`
sets a sleeping NPC awake on the same location.

### `setTablistName(String name, Player... players)`
Sends a packet to update the tablist name (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)

### `setVisibleOnTab(boolean visible, Player... players)`
Sends a add|remove packet for the NPC tablist name (if specific players are chosen, this setting is temporary and will not be saved permanently to NpcData)

### `playLabymodEmote(int emoteId, Player... players)`
sends emote message to make the npc do an specific emote

### `playLabymodSticker(short emoteId, Player... players)`
sends emote message to make the npc do an specific sticker

### `setBow(boolean drawBow, Player... players)`
set the bow for the NPC

### `fishingHook(boolean hook, Player... players)`
throw an fishing hook for the NPC if he has one in the main Hand.

### `playMotionCapture(final String name)`
plays the MotionCapture with the give name, 
if it is currently one MotionCapture for this NPC running the method will produce an **MotionCaptureCurrentlyRunningException**.

### `playMotionCapture(final MotionCaptureData data)`
plays the MotionCapture with the give `date.GetName()`, 
if it is currently one MotionCapture for this NPC running the method will produce an **MotionCaptureCurrentlyRunningException**.

### `sneak(boolean sneak, Player... players)`
makes the NPC sneaking.

### `setItemInHand(final ItemStack item, final Player... players)`
sets a Item in the Mainhand of the NPC.

### `addPotionEffect(MobEffect effect, final Player... players)`
adds the NPC an PotionEffect.
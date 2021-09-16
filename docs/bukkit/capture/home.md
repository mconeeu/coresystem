#MotionCapture
This is the Documentation of the MotionCapture System. 
With this system you can record the movements of players and play them back using NPCs.
All captures are stored in the motion capture collection of the database mc1_data.
All MotionCapture files are located in the `eu.mcone.coresystem.api.bukkit.npc.capture` Package.

You can get back the **MotionCaptureHandler** as follows:
```java 
CoreSystem.getInstance().getNpcManager().getMotionCaptureHandler()
```

### `loadDatabase()`
With this Method you can cache all motion Captures in the Database locally.
Be careful with this method because everything here is loaded into memory!

### `saveMotionCapture(MotionRecorder recorder)`
This Method saves the given MotionRecorder from the Method head in the Database. 
If it exists already an MotionCapture with the same Name the Method throws an **MotionCaptureAlreadyExists** Exception.

### `getMotionCapture(String name)`
This Method returns a `MotionCaptureData` object for the given name.
This object contains all Informations about the MotionCapture. 
Among other things the `creator`, `length` or the most important thing the `motionData`.
If it doesn't exist an _MotionCapture_ for the given name the Method will throw an **MotionCaptureNotFound** Exception.

### `deleteMotionCapture(MotionCaptureData data)`
This Method deletes an _MotionCapture_ for the given name (`data.getName()`).

### `deleteMotionCapture(String name)`
This Method deletes an _MotionCapture_ for the given name.

### `existsMotionCapture(String name)`
Checks if it exists an _MotionCapture_ with the given Name in the Database.

### `getMotionCaptures()`
This method returns all locally stored _MotionCaptures_ as a List of `MotionCaptureData` objects.

#### MotioCaptureData
The MotionCaptureData object stores all relevant Informations about the MotionCapture.
This is also the Object what gets stored in the database.

|  field       | typ     | description | 
| :---         | :---    | :---          
| name         | String  | The name of the MotionCapture 
| world        | String  | The name of the world      
| recorded     | long    | The record date as Unix timestamp    
| creator      | String  | The name of the creator
| length       | int     | The length of the MotionCapture 
| motionData   | Map     | The recorded Packets  


##SimplePlayer/MotionPlayer
This class is needed to play back MotionCapture already stored in the database for an Npc. 
This class is also used for the ReplaySystem.

### `play()`
This is an abstract method, this is used to play the motion capture.

### `stopPlaying()`
This stops the currently playing MotionCapture (The capture is only paused).

### `startPlaying()`
This starts the currently MotionCapture.

### `backward()`
Plays the currently running MotionCapture backward.

### `backward()`
Plays the currently running MotionCapture forward.

### `stop()`
This stops the currently playing MotionCapture (This stops the Capture).

### `addWatcher(Player player)`
This adds a watcher to the currently running MotionCapture, only watchers can see the movements of the currently Playing MotionCapture.
 
### `removeWatcher(Player player)`
This removes the given Player from the watcher list.

### `getWatchers()`
Returns a list of all currently watching Players.

### `getCurrentTick()`
Returns the current Tick of the MotionCapture as AtomicInteger.
 
##MotionCaptureScheduler
This is Util Class, with this Class you can Schedule the already set MotionCapture from the NPC every time it ends.

### `addNpcs(PlayerNpc... npcs)`
This method adds multiple NPCs to the Scheduler queue.

### `addNpc(PlayerNpc npc)`
This method adds a NPCs to the Scheduler queue.
If for the NPC isn't set an MotionCapture the Method will throw an **MotionCaptureNotDefined** Exception.

### `addNpc(PlayerNpc npc, MotionCaptureData data)`
This method sets an MotionCapture for the NPC and add the npc to the Scheduler queue.

### `removeNpc(PlayerNpc npc)`
Removes the given player from the Scheduler queue.

### `removeNpc(String name)`
This method sets an MotionCapture for the NPC and add the npc to the Scheduler queue.

### `getNpcs()`
This method returns a list of all NPCs in the Scheduler queue.
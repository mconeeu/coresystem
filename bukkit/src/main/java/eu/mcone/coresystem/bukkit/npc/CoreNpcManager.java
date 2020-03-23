/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.npc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcManagerReloadedEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.NpcCreateException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.NpcCMD;
import eu.mcone.coresystem.bukkit.listener.NpcListener;
import eu.mcone.coresystem.bukkit.npc.capture.MotionCaptureHandler;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CoreNpcManager implements NpcManager {

    @Getter
    private Set<CoreNPC<?, ?>> npcSet;
    @Getter
    private MotionCaptureHandler motionCaptureHandler;

    public CoreNpcManager(BukkitCoreSystem instance) {
        instance.registerEvents(new NpcListener(instance, this));
        instance.registerCommands(new NpcCMD(this));

        motionCaptureHandler = new MotionCaptureHandler();
        motionCaptureHandler.loadDatabase();

        reload();
    }

    @Override
    public void reload() {
        if (this.npcSet != null) {
            for (NPC npc : npcSet) {
                npc.togglePlayerVisibility(ListMode.WHITELIST);
            }
        } else {
            this.npcSet = new HashSet<>();
        }

        npcSet.clear();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (NpcData data : ((BukkitCoreWorld) w).getNpcData()) {
                addNPC(data);
            }
        }

        Bukkit.getPluginManager().callEvent(new NpcManagerReloadedEvent(this));
    }

    public void addNPCAndSave(EntityType entity, String name, String displayname, Location location) {
        NPC npc = addNPC(new NpcData(entity, name, displayname, new eu.mcone.coresystem.api.bukkit.world.CoreLocation(location), new JsonObject()));

        BukkitCoreWorld world = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(location.getWorld());
        world.getNpcData().add(npc.getData());
        world.save();
    }

    @Override
    public NPC addNPC(NpcData data) {
        return addNPC(data, ListMode.BLACKLIST);
    }

    @Override
    public NPC addNPC(NpcData data, ListMode listMode, Player... players) {
        for (CoreNPC<?, ?> npc : npcSet) {
            if (npc.getData().getLocation().getWorld().equals(data.getLocation().getWorld()) && npc.getData().getName().equalsIgnoreCase(data.getName())) {
                throw new NpcCreateException("Could not create NPC +" + data.getName() + ": NPC with that name already exists in the world" + npc.getData().getLocation().getWorld() + "!");
            }
        }

        CoreNPC<?, ?> npc = null;
        for (NpcType type : NpcType.values()) {
            if (data.getType().equals(type.getType())) {
                npc = type.construct(data, listMode, players);
                break;
            }
        }

        if (npc != null) {
            npcSet.add(npc);
            return npc;
        } else {
            throw new NpcCreateException("Could not create NPC " + data.getName() + ": EntityType " + data.getType() + " does not belong to any NPC type.");
        }
    }

    public void updateAndSave(NPC npc, String displayname, Location location) {
        npc.update(new NpcData(
                npc.getData().getType(),
                npc.getData().getName(),
                displayname,
                new CoreLocation(location),
                npc.getData().getEntityData()
        ));
        BukkitCoreSystem.getSystem().getWorldManager().getWorld(location.getWorld()).save();
    }

    public void updateDataAndSave(NPC npc, JsonElement data) {
        npc.update(new NpcData(
                npc.getData().getType(),
                npc.getData().getName(),
                npc.getData().getDisplayname(),
                npc.getData().getLocation(),
                data
        ));
        BukkitCoreSystem.getSystem().getWorldManager().getWorld(npc.getData().getLocation().getWorld()).save();
    }

    public void removeNPCAndSave(NPC npc) {
        removeNPC(npc);

        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(
                npc.getData().getLocation().getWorld()
        );
        w.getNpcData().remove(npc.getData());
        w.save();
    }

    @Override
    public void removeNPC(NPC npc) {
        npc.togglePlayerVisibility(ListMode.WHITELIST);
        npcSet.remove(npc);
    }

    @Override
    public NPC getNPC(CoreWorld world, String name) {
        for (CoreNPC<?, ?> npc : npcSet) {
            if (npc.getData().getLocation().bukkit().getWorld().getName().equals(world.getName()) && npc.getData().getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }

    @Override
    public NPC getNPC(int entityId) {
        for (CoreNPC<?, ?> npc : npcSet) {
            if (npc.getEntity().getId() == entityId) {
                return npc;
            }
        }
        return null;
    }

    @Override
    public Collection<NPC> getNpcs() {
        return new ArrayList<>(npcSet);
    }

    public void disable() {
        for (NPC npc : npcSet) {
            npc.togglePlayerVisibility(ListMode.WHITELIST);
        }

        npcSet.clear();
    }

}

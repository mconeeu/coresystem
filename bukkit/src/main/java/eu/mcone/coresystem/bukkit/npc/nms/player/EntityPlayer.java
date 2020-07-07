package eu.mcone.coresystem.bukkit.npc.nms.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

public class EntityPlayer extends EntityHuman implements ICrafting {

    private static final Logger bH = LogManager.getLogger();
    public String locale = "en_US";
    public PlayerConnection playerConnection;
    public final MinecraftServer server;
    public double d;
    public double e;
    public final List<ChunkCoordIntPair> chunkCoordIntPairQueue = Lists.newLinkedList();
    public final List<Integer> removeQueue = Lists.newLinkedList();
    private final ServerStatisticManager bK;
    private float bL = 1.4E-45F;
    private float bM = -1.0E8F;
    private int bN = -99999999;
    private boolean bO = true;
    public int lastSentExp = -99999999;
    public int invulnerableTicks = 60;
    private EnumChatVisibility bR;
    private boolean bS = true;
    private long bT = System.currentTimeMillis();
    private Entity bU = null;
    private int containerCounter;
    public boolean g;
    public int ping;
    public boolean viewingCredits;
    public String displayName;
    public IChatBaseComponent listName;
    public Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public boolean collidesWithEntities = true;
    public long timeOffset = 0L;
    public boolean relativeTime = true;
    public WeatherType weather = null;
    private float pluginRainPosition;
    private float pluginRainPositionPrevious;

    public boolean ad() {
        return this.collidesWithEntities && super.ad();
    }

    public boolean ae() {
        return this.collidesWithEntities && super.ae();
    }

    public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(worldserver, gameprofile);
        BlockPosition blockposition = worldserver.getSpawn();
        if (!worldserver.worldProvider.o() && worldserver.getWorldData().getGameType() != WorldSettings.EnumGamemode.ADVENTURE) {
            int i = Math.max(5, minecraftserver.getSpawnProtection() - 6);
            int j = MathHelper.floor(worldserver.getWorldBorder().b(blockposition.getX(), blockposition.getZ()));
            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            blockposition = worldserver.r(blockposition.a(this.random.nextInt(i * 2) - i, 0, this.random.nextInt(i * 2) - i));
        }

        this.server = minecraftserver;
        this.bK = minecraftserver.getPlayerList().a(this);
        this.S = 0.0F;
        this.setPositionRotation(blockposition, 0.0F, 0.0F);

        while (!worldserver.getCubes(this, this.getBoundingBox()).isEmpty() && this.locY < 255.0D) {
            this.setPosition(this.locX, this.locY + 1.0D, this.locZ);
        }

        this.displayName = this.getName();
        this.maxHealthCache = (double) this.getMaxHealth();
    }

    public void spawnIn(World world) {
        super.spawnIn((World) world);
        if (world == null) {
            this.dead = false;
            BlockPosition position = null;
            if (this.spawnWorld != null && !this.spawnWorld.equals("")) {
                CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(this.spawnWorld);
                if (cworld != null && this.getBed() != null) {
                    world = cworld.getHandle();
                    position = net.minecraft.server.v1_8_R3.EntityHuman.getBed(cworld.getHandle(), this.getBed(), false);
                }
            }

            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = ((World) world).getSpawn();
            }

            this.world = (World) world;
            this.setPosition((double) position.getX() + 0.5D, (double) position.getY(), (double) position.getZ() + 0.5D);
        }

        this.dimension = ((WorldServer) this.world).dimension;
    }

    public void levelDown(int i) {
        super.levelDown(i);
        this.lastSentExp = -1;
    }

    public void enchantDone(int i) {
        super.enchantDone(i);
        this.lastSentExp = -1;
    }

    public void syncInventory() {
        this.activeContainer.addSlotListener(this);
    }

    public void enterCombat() {
        super.enterCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.bs(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTER_COMBAT));
    }

    public void exitCombat() {
        super.exitCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.bs(), PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT));
    }

//    public void t_() {
//        if (this.joining) {
//            this.joining = false;
//        }
//
//        this.playerInteractManager.a();
//        --this.invulnerableTicks;
//        if (this.noDamageTicks > 0) {
//            --this.noDamageTicks;
//        }
//
//        this.activeContainer.b();
//        if (!this.world.isClientSide && !this.activeContainer.a(this)) {
//            this.closeInventory();
//            this.activeContainer = this.defaultContainer;
//        }
//
//        while(!this.removeQueue.isEmpty()) {
//            int i = Math.min(this.removeQueue.size(), 2147483647);
//            int[] aint = new int[i];
//            Iterator iterator = this.removeQueue.iterator();
//            int j = 0;
//
//            while(iterator.hasNext() && j < i) {
//                aint[j++] = (Integer)iterator.next();
//                iterator.remove();
//            }
//
//            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(aint));
//        }
//
//        if (!this.chunkCoordIntPairQueue.isEmpty()) {
//            ArrayList arraylist = Lists.newArrayList();
//            Iterator iterator1 = this.chunkCoordIntPairQueue.iterator();
//            ArrayList arraylist1 = Lists.newArrayList();
//
//            Chunk chunk;
//            while(iterator1.hasNext() && arraylist.size() < this.world.spigotConfig.maxBulkChunk) {
//                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)iterator1.next();
//                if (chunkcoordintpair != null) {
//                    if (this.world.isLoaded(new BlockPosition(chunkcoordintpair.x << 4, 0, chunkcoordintpair.z << 4))) {
//                        chunk = this.world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
//                        if (chunk.isReady()) {
//                            arraylist.add(chunk);
//                            arraylist1.addAll(chunk.tileEntities.values());
//                            iterator1.remove();
//                        }
//                    }
//                } else {
//                    iterator1.remove();
//                }
//            }
//
//            if (!arraylist.isEmpty()) {
//                if (arraylist.size() == 1) {
//                    this.playerConnection.sendPacket(new PacketPlayOutMapChunk((Chunk)arraylist.get(0), true, 65535));
//                } else {
//                    this.playerConnection.sendPacket(new PacketPlayOutMapChunkBulk(arraylist));
//                }
//
//                Iterator iterator2 = arraylist1.iterator();
//
//                while(iterator2.hasNext()) {
//                    TileEntity tileentity = (TileEntity)iterator2.next();
//                    this.a(tileentity);
//                }
//
//                iterator2 = arraylist.iterator();
//
//                while(iterator2.hasNext()) {
//                    chunk = (Chunk)iterator2.next();
//                    this.u().getTracker().a(this.bU, chunk);
//                }
//            }
//        }
//
//        Entity entity = this.C();
//        if (entity != this) {
//            if (!entity.isAlive()) {
//                this.setSpectatorTarget(this);
//            } else {
//                this.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
//                this.server.getPlayerList().d(this);
//                if (this.isSneaking()) {
//                    this.setSpectatorTarget(this);
//                }
//            }
//        }
//
//    }

//    public void l() {
//        try {
//            super.t_();
//
//            for(int i = 0; i < this.inventory.getSize(); ++i) {
//                ItemStack itemstack = this.inventory.getItem(i);
//                if (itemstack != null && itemstack.getItem().f()) {
//                    Packet packet = ((ItemWorldMapBase)itemstack.getItem()).c(itemstack, this.world, this);
//                    if (packet != null) {
//                        this.playerConnection.sendPacket(packet);
//                    }
//                }
//            }
//
//            if (this.getHealth() != this.bM || this.bN != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.bO) {
//                this.playerConnection.sendPacket(new PacketPlayOutUpdateHealth(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
//                this.bM = this.getHealth();
//                this.bN = this.foodData.getFoodLevel();
//                this.bO = this.foodData.getSaturationLevel() == 0.0F;
//            }
//
//            if (this.getHealth() + this.getAbsorptionHearts() != this.bL) {
//                this.bL = this.getHealth() + this.getAbsorptionHearts();
//                Collection collection = this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.g);
//                Iterator iterator = collection.iterator();
//
//                while(iterator.hasNext()) {
//                    ScoreboardObjective scoreboardobjective = (ScoreboardObjective)iterator.next();
//                    this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).updateForList(Arrays.asList(this));
//                }
//
//                this.world.getServer().getScoreboardManager().updateAllScoresForList(IScoreboardCriteria.g, this.getName(), ImmutableList.of(this));
//            }
//
//            if (this.maxHealthCache != (double)this.getMaxHealth()) {
//                this.getBukkitEntity().updateScaledHealth();
//            }
//
//            if (this.expTotal != this.lastSentExp) {
//                this.lastSentExp = this.expTotal;
//                this.playerConnection.sendPacket(new PacketPlayOutExperience(this.exp, this.expTotal, this.expLevel));
//            }
//
//            if (this.ticksLived % 20 * 5 == 0 && !this.getStatisticManager().hasAchievement(AchievementList.L)) {
//                this.i_();
//            }
//
//            if (this.oldLevel == -1) {
//                this.oldLevel = this.expLevel;
//            }
//
//            if (this.oldLevel != this.expLevel) {
//                CraftEventFactory.callPlayerLevelChangeEvent(this.world.getServer().getPlayer(this), this.oldLevel, this.expLevel);
//                this.oldLevel = this.expLevel;
//            }
//
//        } catch (Throwable var4) {
//            CrashReport crashreport = CrashReport.a(var4, "Ticking player");
//            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");
//            this.appendEntityCrashDetails(crashreportsystemdetails);
//            throw new ReportedException(crashreport);
//        }
//    }
//
//    protected void i_() {
//        BiomeBase biomebase = this.world.getBiome(new BlockPosition(MathHelper.floor(this.locX), 0, MathHelper.floor(this.locZ)));
//        String s = biomebase.ah;
//        AchievementSet achievementset = (AchievementSet)this.getStatisticManager().b(AchievementList.L);
//        if (achievementset == null) {
//            achievementset = (AchievementSet)this.getStatisticManager().a(AchievementList.L, new AchievementSet());
//        }
//
//        achievementset.add(s);
//        if (this.getStatisticManager().b(AchievementList.L) && achievementset.size() >= BiomeBase.n.size()) {
//            HashSet hashset = Sets.newHashSet(BiomeBase.n);
//            Iterator iterator = achievementset.iterator();
//
//            while(iterator.hasNext()) {
//                String s1 = (String)iterator.next();
//                Iterator iterator1 = hashset.iterator();
//
//                while(iterator1.hasNext()) {
//                    BiomeBase biomebase1 = (BiomeBase)iterator1.next();
//                    if (biomebase1.ah.equals(s1)) {
//                        iterator1.remove();
//                    }
//                }
//
//                if (hashset.isEmpty()) {
//                    break;
//                }
//            }
//
//            if (hashset.isEmpty()) {
//                this.b((Statistic)AchievementList.L);
//            }
//        }
//
//    }

    public void die(DamageSource damagesource) {
        if (!this.dead) {
            List<org.bukkit.inventory.ItemStack> loot = new ArrayList();
            boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
            if (!keepInventory) {
                int i;
                for (i = 0; i < this.inventory.items.length; ++i) {
                    if (this.inventory.items[i] != null) {
                        loot.add(CraftItemStack.asCraftMirror(this.inventory.items[i]));
                    }
                }

                for (i = 0; i < this.inventory.armor.length; ++i) {
                    if (this.inventory.armor[i] != null) {
                        loot.add(CraftItemStack.asCraftMirror(this.inventory.armor[i]));
                    }
                }
            }

            IChatBaseComponent chatmessage = this.bs().b();
            String deathmessage = chatmessage.c();
//            PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);
//            String deathMessage = event.getDeathMessage();
//            if (deathMessage != null && deathMessage.length() > 0 && this.world.getGameRules().getBoolean("showDeathMessages")) {
//                if (deathMessage.equals(deathmessage)) {
//                    this.server.getPlayerList().sendMessage(chatmessage);
//                } else {
//                    this.server.getPlayerList().sendMessage(CraftChatMessage.fromString(deathMessage));
//                }
//            }
//
//            if (!event.getKeepInventory()) {
//                int i;
//                for(i = 0; i < this.inventory.items.length; ++i) {
//                    this.inventory.items[i] = null;
//                }
//
//                for(i = 0; i < this.inventory.armor.length; ++i) {
//                    this.inventory.armor[i] = null;
//                }
//            }

            this.closeInventory();
            this.setSpectatorTarget(this);
            Collection collection = this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.d, this.getName(), new ArrayList());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();
                scoreboardscore.incrementScore();
            }

            EntityLiving entityliving = this.bt();
            if (entityliving != null) {
                EntityTypes.MonsterEggInfo entitytypes_monsteregginfo = (EntityTypes.MonsterEggInfo) EntityTypes.eggInfo.get(EntityTypes.a(entityliving));
                if (entitytypes_monsteregginfo != null) {
                    this.b((Statistic) entitytypes_monsteregginfo.e);
                }

                entityliving.b(this, this.aW);
            }

            this.b((Statistic) StatisticList.y);
            this.a(StatisticList.h);
            this.bs().g();
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            boolean flag = this.server.ae() && this.cr() && "fall".equals(damagesource.translationIndex);
            if (!flag && this.invulnerableTicks > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (damagesource instanceof EntityDamageSource) {
                    Entity entity = damagesource.getEntity();
                    if (entity instanceof net.minecraft.server.v1_8_R3.EntityHuman && !this.a((net.minecraft.server.v1_8_R3.EntityHuman) entity)) {
                        return false;
                    }

                    if (entity instanceof EntityArrow) {
                        EntityArrow entityarrow = (EntityArrow) entity;
                        if (entityarrow.shooter instanceof net.minecraft.server.v1_8_R3.EntityHuman && !this.a((net.minecraft.server.v1_8_R3.EntityHuman) entityarrow.shooter)) {
                            return false;
                        }
                    }
                }

                return super.damageEntity(damagesource, f);
            }
        }
    }

    public boolean a(net.minecraft.server.v1_8_R3.EntityHuman entityhuman) {
        return !this.cr() ? false : super.a(entityhuman);
    }

    private boolean cr() {
        return this.world.pvpMode;
    }

    public boolean a(net.minecraft.server.v1_8_R3.EntityPlayer entityplayer) {
        return entityplayer.isSpectator() ? this.C() == this : (this.isSpectator() ? false : super.a(entityplayer));
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.getUpdatePacket();
            if (packet != null) {
                this.playerConnection.sendPacket(packet);
            }
        }

    }

    public void receive(Entity entity, int i) {
        super.receive(entity, i);
        this.activeContainer.b();
    }

    public EnumBedResult a(BlockPosition blockposition) {
        EnumBedResult entityhuman_enumbedresult = super.a(blockposition);
        if (entityhuman_enumbedresult == EnumBedResult.OK) {
            PacketPlayOutBed packetplayoutbed = new PacketPlayOutBed(this, blockposition);
            this.u().getTracker().a(this, packetplayoutbed);
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.playerConnection.sendPacket(packetplayoutbed);
        }

        return entityhuman_enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (this.sleeping) {
            if (this.isSleeping()) {
                this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
            }

            super.a(flag, flag1, flag2);
            if (this.playerConnection != null) {
                this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            }

        }
    }

    public void mount(Entity entity) {
        Entity entity1 = this.vehicle;
        super.mount(entity);
        if (this.vehicle != entity1) {
            this.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this, this.vehicle));
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }

    }

    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
    }

    public void a(double d0, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        Block block = this.world.getType(blockposition).getBlock();
        if (block.getMaterial() == Material.AIR) {
            Block block1 = this.world.getType(blockposition.down()).getBlock();
            if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                blockposition = blockposition.down();
                block = this.world.getType(blockposition).getBlock();
            }
        }

        super.a(d0, flag, block, blockposition);
    }

    public void openSign(TileEntitySign tileentitysign) {
        tileentitysign.a(this);
        this.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(tileentitysign.getPosition()));
    }

    public int nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
        return this.containerCounter;
    }

    public void openBook(ItemStack itemstack) {
        Item item = itemstack.getItem();
        if (item == Items.WRITTEN_BOOK) {
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
        }

    }

    public void a(Container container, int i, ItemStack itemstack) {
        if (!(container.getSlot(i) instanceof SlotResult) && !this.g) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, i, itemstack));
        }

    }

    public void updateInventory(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, List<ItemStack> list) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowItems(container.windowId, list));
        this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        if (EnumSet.of(InventoryType.CRAFTING, InventoryType.WORKBENCH).contains(container.getBukkitView().getType())) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
        }

    }

    public void setContainerData(Container container, int i, int j) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, j));
    }

    public void setContainerData(Container container, IInventory iinventory) {
        for (int i = 0; i < iinventory.g(); ++i) {
            this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, iinventory.getProperty(i)));
        }

    }

    public void closeInventory() {
        CraftEventFactory.handleInventoryCloseEvent(this);
        this.playerConnection.sendPacket(new PacketPlayOutCloseWindow(this.activeContainer.windowId));
        this.p();
    }

    public void broadcastCarriedItem() {
        if (!this.g) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        }

    }

    public void p() {
        this.activeContainer.b(this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(float f, float f1, boolean flag, boolean flag1) {
        if (this.vehicle != null) {
            if (f >= -1.0F && f <= 1.0F) {
                this.aZ = f;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.ba = f1;
            }

            this.aY = flag;
            this.setSneaking(flag1);
        }

    }

    public void q() {
        if (this.passenger != null) {
            this.passenger.mount(this);
        }

        if (this.sleeping) {
            this.a(true, false, false);
        }

    }

    public void triggerHealthUpdate() {
        this.bM = -1.0E8F;
        this.lastSentExp = -1;
    }

    public void sendMessage(IChatBaseComponent[] ichatbasecomponent) {
        IChatBaseComponent[] var2 = ichatbasecomponent;
        int var3 = ichatbasecomponent.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            IChatBaseComponent component = var2[var4];
            this.sendMessage(component);
        }

    }

    public void b(IChatBaseComponent ichatbasecomponent) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
    }

    protected void s() {
        this.playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 9));
        super.s();
    }

    public void a(ItemStack itemstack, int i) {
        super.a(itemstack, i);
        if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().e(itemstack) == EnumAnimation.EAT) {
            this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 3));
        }

    }

    public void copyTo(net.minecraft.server.v1_8_R3.EntityHuman entityhuman, boolean flag) {
        super.copyTo(entityhuman, flag);
        this.lastSentExp = -1;
        this.bM = -1.0F;
        this.bN = -1;
        this.removeQueue.addAll(((net.minecraft.server.v1_8_R3.EntityPlayer) entityhuman).removeQueue);
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
    }

    protected void a(MobEffect mobeffect, boolean flag) {
        super.a(mobeffect, flag);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect));
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.playerConnection.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void b(Entity entity) {
        this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
    }

    public void c(Entity entity) {
        this.u().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
    }

    public void updateAbilities() {
        if (this.playerConnection != null) {
            this.playerConnection.sendPacket(new PacketPlayOutAbilities(this.abilities));
            this.B();
        }

    }

    public WorldServer u() {
        return (WorldServer) this.world;
    }

    public void a(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
        this.getBukkitEntity().setGameMode(GameMode.getByValue(worldsettings_enumgamemode.getId()));
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
    }

    public boolean a(int i, String s) {
        return "@".equals(s) ? this.getBukkitEntity().hasPermission("minecraft.command.selector") : true;
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        this.locale = packetplayinsettings.a();
        this.bR = packetplayinsettings.c();
        this.bS = packetplayinsettings.d();
        this.getDataWatcher().watch(10, (byte) packetplayinsettings.e());
    }

    public EnumChatVisibility getChatFlags() {
        return this.bR;
    }

    public void setResourcePack(String s, String s1) {
        this.playerConnection.sendPacket(new PacketPlayOutResourcePackSend(s, s1));
    }

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this.locX, this.locY + 0.5D, this.locZ);
    }

    public void resetIdleTimer() {
        this.bT = MinecraftServer.az();
    }

    public ServerStatisticManager getStatisticManager() {
        return this.bK;
    }

    public void d(Entity entity) {
        if (entity instanceof net.minecraft.server.v1_8_R3.EntityHuman) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{entity.getId()}));
        } else {
            this.removeQueue.add(entity.getId());
        }

    }

    public Entity C() {
        return (Entity) (this.bU == null ? this : this.bU);
    }

    public void setSpectatorTarget(Entity entity) {
        Entity entity1 = this.C();
        this.bU = (Entity) (entity == null ? this : entity);
        if (entity1 != this.bU) {
            this.playerConnection.sendPacket(new PacketPlayOutCamera(this.bU));
            this.enderTeleportTo(this.bU.locX, this.bU.locY, this.bU.locZ);
        }

    }

    public void attack(Entity entity) {
        super.attack(entity);
    }

    public IChatBaseComponent getPlayerListName() {
        return this.listName;
    }

    public long getPlayerTime() {
        return this.relativeTime ? this.world.getDayTime() + this.timeOffset : this.world.getDayTime() - this.world.getDayTime() % 24000L + this.timeOffset;
    }

    public WeatherType getPlayerWeather() {
        return this.weather;
    }

    public void setPlayerWeather(WeatherType type, boolean plugin) {
        if (plugin || this.weather == null) {
            if (plugin) {
                this.weather = type;
            }

            if (type == WeatherType.DOWNFALL) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0.0F));
            } else {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0.0F));
            }

        }
    }

    public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        if (this.weather == null) {
            if (oldRain != newRain) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, newRain));
            }
        } else if (this.pluginRainPositionPrevious != this.pluginRainPosition) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, this.pluginRainPosition));
        }

        if (oldThunder != newThunder) {
            if (this.weather != WeatherType.DOWNFALL && this.weather != null) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 0.0F));
            } else {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, newThunder));
            }
        }

    }

    public void tickWeather() {
        if (this.weather != null) {
            this.pluginRainPositionPrevious = this.pluginRainPosition;
            if (this.weather == WeatherType.DOWNFALL) {
                this.pluginRainPosition = (float) ((double) this.pluginRainPosition + 0.01D);
            } else {
                this.pluginRainPosition = (float) ((double) this.pluginRainPosition - 0.01D);
            }

            this.pluginRainPosition = MathHelper.a(this.pluginRainPosition, 0.0F, 1.0F);
        }
    }

    public void resetPlayerWeather() {
        this.weather = null;
        this.setPlayerWeather(this.world.getWorldData().hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
    }

    public String toString() {
        return super.toString() + "(" + this.getName() + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
    }

    public void reset() {
        float exp = 0.0F;
        boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
        if (this.keepLevel || keepInventory) {
            exp = this.exp;
            this.newTotalExp = this.expTotal;
            this.newLevel = this.expLevel;
        }

        this.setHealth(this.getMaxHealth());
        this.fireTicks = 0;
        this.fallDistance = 0.0F;
        this.foodData = new FoodMetaData(this);
        this.expLevel = this.newLevel;
        this.expTotal = this.newTotalExp;
        this.exp = 0.0F;
        this.deathTicks = 0;
        this.removeAllEffects();
        this.updateEffects = true;
        this.activeContainer = this.defaultContainer;
        this.killer = null;
        this.lastDamager = null;
        this.combatTracker = new CombatTracker(this);
        this.lastSentExp = -1;
        if (!this.keepLevel && !keepInventory) {
            this.giveExp(this.newExp);
        } else {
            this.exp = exp;
        }

        this.keepLevel = false;
    }

    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }
}

package me.yamakaja.bukkitjs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.*;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.event.hanging.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.*;
import org.bukkit.event.world.*;

public class EventManager implements Listener,EventExecutor{
	
	BukkitJs plugin;
	
	public Map<String,Boolean> registeredListeners = new HashMap<>();
	
	public EventManager(BukkitJs plugin){
		this.plugin = plugin;
	}
	
	public void registerEvent(Class<? extends Event> clazz){
		
		if(!(clazz == null) && !(registeredListeners.containsKey(clazz.getName()))){
			plugin.getServer().getPluginManager().registerEvent(clazz, this, EventPriority.NORMAL, this, plugin);
			registeredListeners.put(clazz.getName(), Boolean.TRUE);
		}
		
	}
	
	@Override
	public void execute(Listener listener, Event e) throws EventException {
		for(Script s: plugin.scriptManager.scripts){
			if(e.getEventName().equalsIgnoreCase(s.getEventType().name()) && s.enabled){
				plugin.engineManager.execute(s.getName(), e);
			}
		}
	}
	
	public void test(ProjectileLaunchEvent e){
		if(e.getEntityType() == EntityType.ENDER_PEARL){
			e.getEntity().setPassenger((LivingEntity)e.getEntity().getShooter());
		}
	}
	
	public enum EventType{
		AsyncPlayerChatEvent(AsyncPlayerChatEvent.class),
		AsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent.class),
		PlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent.class),
		PlayerAnimationEvent(PlayerAnimationEvent.class),
		PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent.class),
		PlayerBedEnterEvent(PlayerBedEnterEvent.class),
		PlayerBedLeaveEvent(PlayerBedLeaveEvent.class),
		PlayerBucketEmptyEvent(PlayerBucketEmptyEvent.class),
		PlayerBucketEvent(PlayerBucketEvent.class),
		PlayerBucketFillEvent(PlayerBucketFillEvent.class),
		PlayerChangedWorldEvent(PlayerChangedWorldEvent.class),
		PlayerChannelEvent(PlayerChannelEvent.class),
		PlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent.class),
		PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent.class),
		PlayerDropItemEvent(PlayerDropItemEvent.class),
		PlayerEditBookEvent(PlayerEditBookEvent.class),
		PlayerEggThrowEvent(PlayerEggThrowEvent.class),
		PlayerExpChangeEvent(PlayerExpChangeEvent.class),
		PlayerFishEvent(PlayerFishEvent.class),
		PlayerGameModeChangeEvent(PlayerGameModeChangeEvent.class),
		PlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent.class),
		PlayerInteractEntityEvent(PlayerInteractEntityEvent.class),
		PlayerInteractEvent(PlayerInteractEvent.class),
		PlayerItemBreakEvent(PlayerItemBreakEvent.class),
		PlayerItemDamageEvent(PlayerItemDamageEvent.class),
		PlayerItemHeldEvent(PlayerItemHeldEvent.class),
		PlayerJoinEvent(PlayerJoinEvent.class),
		PlayerKickEvent(PlayerKickEvent.class),
		PlayerLevelChangeEvent(PlayerLevelChangeEvent.class),
		PlayerLoginEvent(PlayerLoginEvent.class),
		PlayerMoveEvent(PlayerMoveEvent.class),
		PlayerPickupItemEvent(PlayerPickupItemEvent.class),
		PlayerPortalEvent(PlayerPortalEvent.class),
		PlayerQuitEvent(PlayerQuitEvent.class),
		PlayerRegisterChannelEvent(PlayerRegisterChannelEvent.class),
		PlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent.class),
		PlayerRespawnEvent(PlayerRespawnEvent.class),
		PlayerShearEntityEvent(PlayerShearEntityEvent.class),
		PlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent.class),
		PlayerTeleportEvent(PlayerTeleportEvent.class),
		PlayerToggleFlightEvent(PlayerToggleFlightEvent.class),
		PlayerToggleSneakEvent(PlayerToggleSneakEvent.class),
		PlayerToggleSprintEvent(PlayerToggleSprintEvent.class),
		PlayerUnleashEntityEvent(PlayerUnleashEntityEvent.class),
		PlayerUnregisterChannelEvent(PlayerUnregisterChannelEvent.class),
		PlayerVelocityEvent(PlayerVelocityEvent.class),
		BlockBreakEvent(BlockBreakEvent.class),
		BlockBurnEvent(BlockBurnEvent.class),
		BlockCanBuildEvent(BlockCanBuildEvent.class),
		BlockDamageEvent(BlockDamageEvent.class),
		BlockDispenseEvent(BlockDispenseEvent.class),
		BlockExpEvent(BlockExpEvent.class),
		BlockExplodeEvent(BlockExplodeEvent.class),
		BlockFadeEvent(BlockFadeEvent.class),
		BlockFormEvent(BlockFormEvent.class),
		BlockFormToEvent(BlockFromToEvent.class),
		BlockGrowEvent(BlockGrowEvent.class),
		BlockIgniteEvent(BlockIgniteEvent.class),
		BlockMultiPlaceEvent(BlockMultiPlaceEvent.class),
		BlockPhysicsEvent(BlockPhysicsEvent.class),
		BlockPistonEvent(BlockPistonEvent.class),
		BlockPistonExtendEvent(BlockPistonExtendEvent.class),
		BlockPistonRetractEvent(BlockPistonRetractEvent.class),
		BlockPlaceEvent(BlockPlaceEvent.class),
		BlockRedstoneEvent(BlockRedstoneEvent.class),
		BlockSpreadEvent(BlockSpreadEvent.class),
		EntityBlockFormEvent(EntityBlockFormEvent.class),
		LeavesDecayEvent(LeavesDecayEvent.class),
		NotePlayEvent(NotePlayEvent.class),
		SignChangeEvent(SignChangeEvent.class),
		EnchantItemEvent(EnchantItemEvent.class),
		PrepareItemEnchantEvent(PrepareItemEnchantEvent.class),
		CreatureSpawnEvent(CreatureSpawnEvent.class),
		CreeperPowerEvent(CreeperPowerEvent.class),
		EntityBreakDoorEvent(EntityBreakDoorEvent.class),
		EntityChangeBlockEvent(EntityChangeBlockEvent.class),
		EntityCombustByBlockEvent(EntityCombustByBlockEvent.class),
		EntityCombustByEntityEvent(EntityCombustByEntityEvent.class),
		EntityCombustEvent(EntityCombustEvent.class),
		EntityCreatePortalEvent(EntityCreatePortalEvent.class),
		EntityDamageByBlockEvent(EntityDamageByBlockEvent.class),
		EntityDamageByEntityEvent(EntityDamageByEntityEvent.class),
		EntityDamageEvent(EntityDamageEvent.class),
		EntityDeathEvent(EntityDeathEvent.class),
		EntityExplodeEvent(EntityExplodeEvent.class),
		EntityInteractEvent(EntityInteractEvent.class),
		EntityPortalEnterEvent(EntityPortalEnterEvent.class),
		EntityPortalEvent(EntityPortalEvent.class),
		EntityPortalExitEvent(EntityPortalExitEvent.class),
		EntityRegainHealthEvent(EntityRegainHealthEvent.class),
		EntityShootBowEvent(EntityShootBowEvent.class),
		EntitySpawnEvent(EntitySpawnEvent.class),
		EntityTameEvent(EntityTameEvent.class),
		EntityTargetEvent(EntityTargetEvent.class),
		EntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent.class),
		EntityTeleportEvent(EntityTeleportEvent.class),
		EntityUnleashEvent(EntityUnleashEvent.class),
		ExpBottleEvent(ExpBottleEvent.class),
		ExplosionPrimeEvent(ExplosionPrimeEvent.class),
		FoodLevelChangeEvent(FoodLevelChangeEvent.class),
		HorseJumpEvent(HorseJumpEvent.class),
		ItemDespawnEvent(ItemDespawnEvent.class),
		ItemMergeEvent(ItemMergeEvent.class),
		ItemSpawnEvent(ItemSpawnEvent.class),
		PigZapEvent(PigZapEvent.class),
		PlayerDeathEvent(PlayerDeathEvent.class),
		PlayerLeashEntityEvent(PlayerLeashEntityEvent.class),
		PotionSplashEvent(PotionSplashEvent.class),
		ProjectileHitEvent(ProjectileHitEvent.class),
		ProjectileLaunchEvent(ProjectileLaunchEvent.class),
		SheepDyeWoolEvent(SheepDyeWoolEvent.class),
		SheepRegrowWoolEvent(SheepRegrowWoolEvent.class),
		SlimeSplitEvent(SlimeSplitEvent.class),
		SpawnerSpawnEvent(SpawnerSpawnEvent.class),
		HangingBreakByEntityEvent(HangingBreakByEntityEvent.class),
		HangingBreakEvent(HangingBreakEvent.class),
		HangingPlaceEvent(HangingPlaceEvent.class),
		BrewEvent(BrewEvent.class),
		CraftItemEvent(CraftItemEvent.class),
		FurnaceBurnEvent(FurnaceBurnEvent.class),
		FurnaceExtractEvent(FurnaceExtractEvent.class),
		FurnaceSmeltEvent(FurnaceSmeltEvent.class),
		InventoryClickEvent(InventoryClickEvent.class),
		InventoryCloseEvent(InventoryCloseEvent.class),
		InventoryCreativeEvent(InventoryCreativeEvent.class),
		InventoryDragEvent(InventoryDragEvent.class),
		InventoryInteractEvent(InventoryInteractEvent.class),
		InventoryMoveItemEvent(InventoryMoveItemEvent.class),
		InventoryOpenEvent(InventoryOpenEvent.class),
		InventoryPickupItemEvent(InventoryPickupItemEvent.class),
		PrepareItemCraftEvent(PrepareItemCraftEvent.class),
		MapInitializeEvent(MapInitializeEvent.class),
		PluginDisableEvent(PluginDisableEvent.class),
		PluginEnableEvent(PluginEnableEvent.class),
		RemoteServerCommandEvent(RemoteServerCommandEvent.class),
		ServerCommandEvent(ServerCommandEvent.class),
		ServerListPingEvent(ServerListPingEvent.class),
		ServiceRegisterEvent(ServiceRegisterEvent.class),
		ServiceUnregisterEvent(ServiceUnregisterEvent.class),
		VehicleBlockCollisionEvent(VehicleBlockCollisionEvent.class),
		VehicleCollisionEvent(VehicleCollisionEvent.class),
		VehicleCreateEvent(VehicleCreateEvent.class),
		VehicleDamageEvent(VehicleDamageEvent.class),
		VehicleDestroyEvent(VehicleDestroyEvent.class),
		VehicleEnterEvent(VehicleEnterEvent.class),
		VehicleEntityCollisionEvent(VehicleEntityCollisionEvent.class),
		VehicleExitEvent(VehicleExitEvent.class),
		VehicleMoveEvent(VehicleMoveEvent.class),
		VehicleUpdateEvent(VehicleUpdateEvent.class),
		LightningStrikeEvent(LightningStrikeEvent.class),
		ThunderChangeEvent(ThunderChangeEvent.class),
		WeatherChangeEvent(WeatherChangeEvent.class),
		ChunkLoadEvent(ChunkLoadEvent.class),
		ChunkPopulateEvent(ChunkPopulateEvent.class),
		ChunkUnloadEvent(ChunkUnloadEvent.class),
		PortalCreateEvent(PortalCreateEvent.class),
		SpawnChangeEvent(SpawnChangeEvent.class),
		StructureGrowEvent(StructureGrowEvent.class),
		WorldInitEvent(WorldInitEvent.class),
		WorldLoadEvent(WorldLoadEvent.class),
		WorldSaveEvent(WorldSaveEvent.class),
		WorldUnloadEvent(WorldUnloadEvent.class),
		DUMMY,
		TIMER;
		
		public Class<? extends Event> clazz;
		
		private EventType(Class<? extends Event> clazz){
			if(clazz == null){
				return;
			}
			this.clazz = clazz;
		}
		
		private EventType(){
			clazz = null;
		}
		
		public boolean isDummy(){
			return this == EventType.DUMMY;
		}
		
		public boolean isEvent(){
			return clazz != null;
		}
		
		public boolean isTimer(){
			return this == EventType.TIMER;
		}
	}

}

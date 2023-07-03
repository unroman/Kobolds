package net.salju.kobolds.procedures;

import net.salju.kobolds.worldgen.KoboldRascalSpawner;
import net.salju.kobolds.worldgen.KoboldData;
import net.salju.kobolds.entity.AbstractKoboldEntity;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.HashMap;

@Mod.EventBusSubscriber
public class KoboldsEventsProcedure {
	private static final Map<ServerLevel, KoboldRascalSpawner> RASCAL_MAP = new HashMap<>();

	@SubscribeEvent
	public static void onEntitySpawned(EntityJoinLevelEvent event) {
		Entity target = event.getEntity();
		if (target instanceof Raider johnny) {
			((Mob) johnny).targetSelector.addGoal(3, new NearestAttackableTargetGoal((Mob) johnny, AbstractKoboldEntity.class, false));
		} else if (target instanceof Zombie billy && !(billy instanceof ZombifiedPiglin)) {
			((Mob) billy).targetSelector.addGoal(3, new NearestAttackableTargetGoal((Mob) billy, AbstractKoboldEntity.class, false));
		} else if (target instanceof Villager nose) {
			((Villager) nose).goalSelector.addGoal(1, new AvoidEntityGoal((Villager) nose, AbstractKoboldEntity.class, (float) 6, 0.6, 0.8));
		}
	}

	@SubscribeEvent
	public static void onTick(TickEvent.LevelTickEvent event) {
		if (!event.level.isClientSide && event.level instanceof ServerLevel lvl) {
			KoboldData info = KoboldData.get(lvl);
			RASCAL_MAP.computeIfAbsent(lvl, k -> new KoboldRascalSpawner(info));
			KoboldRascalSpawner spawner = RASCAL_MAP.get(lvl);
			spawner.tick(lvl);
		}
	}
}
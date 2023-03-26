package net.salju.kobolds.procedures;

import net.salju.kobolds.entity.AbstractKoboldEntity;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber
public class KoboldsEventsProcedure {
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
}
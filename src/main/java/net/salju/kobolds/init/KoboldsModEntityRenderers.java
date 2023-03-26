
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.salju.kobolds.init;

import net.salju.kobolds.client.renderer.KoboldZombieRenderer;
import net.salju.kobolds.client.renderer.KoboldZombieDrownedRenderer;
import net.salju.kobolds.client.renderer.KoboldWarriorRenderer;
import net.salju.kobolds.client.renderer.KoboldSkeletonRenderer;
import net.salju.kobolds.client.renderer.KoboldRenderer;
import net.salju.kobolds.client.renderer.KoboldRascalRenderer;
import net.salju.kobolds.client.renderer.KoboldPirateRenderer;
import net.salju.kobolds.client.renderer.KoboldEngineerRenderer;
import net.salju.kobolds.client.renderer.KoboldEnchanterRenderer;
import net.salju.kobolds.client.renderer.KoboldChildRenderer;
import net.salju.kobolds.client.renderer.KoboldCaptainRenderer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KoboldsModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD.get(), KoboldRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_WARRIOR.get(), KoboldWarriorRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_ENCHANTER.get(), KoboldEnchanterRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_ENGINEER.get(), KoboldEngineerRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_PIRATE.get(), KoboldPirateRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_CAPTAIN.get(), KoboldCaptainRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_CHILD.get(), KoboldChildRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_ZOMBIE.get(), KoboldZombieRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_ZOMBIE_DROWNED.get(), KoboldZombieDrownedRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_SKELETON.get(), KoboldSkeletonRenderer::new);
		event.registerEntityRenderer(KoboldsModEntities.KOBOLD_RASCAL.get(), KoboldRascalRenderer::new);
	}
}

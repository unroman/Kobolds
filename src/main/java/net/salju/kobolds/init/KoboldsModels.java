package net.salju.kobolds.init;

import net.salju.kobolds.client.model.*;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class KoboldsModels {

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(KoboldArmorModel.KOBOLD_ARMOR_INNER_MODEL, KoboldArmorModel::createInnerArmorLayer);
		event.registerLayerDefinition(KoboldArmorModel.KOBOLD_ARMOR_OUTER_MODEL, KoboldArmorModel::createOuterArmorLayer);
		event.registerLayerDefinition(KoboldModel.KOBOLD_MODEL, KoboldModel::createBodyLayer);
		event.registerLayerDefinition(RascalModel.RASCAL_MODEL, RascalModel::createBodyLayer);
		event.registerLayerDefinition(KoboldChildModel.KOBOLD_CHILD_MODEL, KoboldChildModel::createBodyLayer);
		event.registerLayerDefinition(ZomboldModel.ZOMBOLD_MODEL, ZomboldModel::createBodyLayer);
		event.registerLayerDefinition(SkeleboldModel.SKELEBOLD_MODEL, SkeleboldModel::createBodyLayer);
	}
}
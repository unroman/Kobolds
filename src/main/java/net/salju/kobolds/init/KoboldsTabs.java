package net.salju.kobolds.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.CreativeModeTabEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KoboldsTabs {
	@SubscribeEvent
	public static void buildTabContentsModded(CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(new ResourceLocation("kobolds_tab"),
				builder -> builder.title(Component.translatable("itemGroup.kobolds_tab")).icon(() -> new ItemStack(KoboldsBlocks.KOBOLD_SKULL.get().asItem())).displayItems((parameters, tabData) -> {
					tabData.accept(KoboldsItems.KOBOLD_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_WARRIOR_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_ENCHANTER_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_ENGINEER_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_PIRATE_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_CAPTAIN_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_ZOMBIE_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_ZOMBIE_DROWNED_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_SKELETON_SPAWN_EGG.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_HEALTH.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_FIRE.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_STEALTH.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_COMBAT.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_WATER.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_LEAPING.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_LEVITATION.get());
					tabData.accept(KoboldsItems.KOBOLD_POTION_MINING.get());
					tabData.accept(KoboldsBlocks.KOBOLD_SKULL.get().asItem());
					tabData.accept(KoboldsItems.KOBOLD_INFINITY_POTION.get());
					tabData.accept(KoboldsItems.KOBOLD_IRON_SWORD.get());
					tabData.accept(KoboldsItems.KOBOLD_IRON_SHOVEL.get());
					tabData.accept(KoboldsItems.KOBOLD_IRON_PICKAXE.get());
					tabData.accept(KoboldsItems.KOBOLD_IRON_AXE.get());
					tabData.accept(KoboldsItems.KOBOLD_IRON_HOE.get());
					tabData.accept(KoboldsItems.BANNER_PATTERN_KOBOLD.get());
					tabData.accept(KoboldsItems.MUSIC_DISC_KOBBLESTONE.get());
					tabData.accept(KoboldsItems.KOBOLD_TEMPLATE.get());
				}));
	}
}
package net.salju.kobolds.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;

public class KoboldsTabs {
	public static CreativeModeTab KOBOLD_CREATIVE_TAB;

	public static void load() {
		KOBOLD_CREATIVE_TAB = new CreativeModeTab("kobolds_tab") {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(KoboldsBlocks.KOBOLD_SKULL.get());
			}

			@Override
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
}
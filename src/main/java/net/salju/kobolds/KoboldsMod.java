package net.salju.kobolds;

import net.salju.kobolds.init.*;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("kobolds")
public class KoboldsMod {
	public static final String MODID = "kobolds";

	public KoboldsMod() {
		MinecraftForge.EVENT_BUS.register(this);
		KoboldsTabs.load();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		KoboldsModSounds.REGISTRY.register(bus);
		KoboldsBlocks.REGISTRY.register(bus);
		KoboldsItems.REGISTRY.register(bus);
		KoboldsBanners.REGISTRY.register(bus);
		KoboldsModEntities.REGISTRY.register(bus);
		KoboldsEnchantments.REGISTRY.register(bus);
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
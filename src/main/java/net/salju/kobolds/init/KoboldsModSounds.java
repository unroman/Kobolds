
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.salju.kobolds.init;

import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class KoboldsModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, KoboldsMod.MODID);
	public static final RegistryObject<SoundEvent> KOBOLD_TRADE = REGISTRY.register("kobold_trade", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "kobold_trade")));
	public static final RegistryObject<SoundEvent> KOBOLD_PURR = REGISTRY.register("kobold_purr", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "kobold_purr")));
	public static final RegistryObject<SoundEvent> KOBOLD_DEATH = REGISTRY.register("kobold_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "kobold_death")));
	public static final RegistryObject<SoundEvent> KOBOLD_HURT = REGISTRY.register("kobold_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "kobold_hurt")));
	public static final RegistryObject<SoundEvent> KOBOLD_IDLE = REGISTRY.register("kobold_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "kobold_idle")));
	public static final RegistryObject<SoundEvent> MUSIC_KOBBLESTONE = REGISTRY.register("music_kobblestone", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("kobolds", "music_kobblestone")));
}

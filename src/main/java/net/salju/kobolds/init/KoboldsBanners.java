package net.salju.kobolds.init;

import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.registries.Registries;

public class KoboldsBanners {
	public static final DeferredRegister<BannerPattern> REGISTRY = DeferredRegister.create(Registries.BANNER_PATTERN, KoboldsMod.MODID);
	public static final RegistryObject<BannerPattern> KOBOLD_BANNER = REGISTRY.register("kobold", () -> new BannerPattern("kobold"));
}
package net.salju.kobolds.init;

import net.salju.kobolds.block.*;
import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class KoboldsBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, KoboldsMod.MODID);
	public static final RegistryObject<Block> KOBOLD_SKULL = REGISTRY.register("kobold_skull", () -> new KoboldSkull());
}
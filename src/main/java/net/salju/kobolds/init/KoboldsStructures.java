package net.salju.kobolds.init;

import net.salju.kobolds.worldgen.AbstractKoboldStructure;
import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.core.registries.Registries;

import com.mojang.serialization.Codec;

public class KoboldsStructures {
	public static final DeferredRegister<StructureType<?>> REGISTRY = DeferredRegister.create(Registries.STRUCTURE_TYPE, KoboldsMod.MODID);
	public static final RegistryObject<StructureType<AbstractKoboldStructure>> KOBOLD_STRUCTURE = REGISTRY.register("abstractkoboldstructure", () -> stuff(AbstractKoboldStructure.CODEC));

	private static <T extends Structure> StructureType<T> stuff(Codec<T> codec) {
		return () -> codec;
	}
}
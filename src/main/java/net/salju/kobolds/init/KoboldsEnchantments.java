package net.salju.kobolds.init;

import net.salju.kobolds.enchantment.*;
import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

public class KoboldsEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, KoboldsMod.MODID);
	public static final RegistryObject<Enchantment> PROSPECTOR = REGISTRY.register("prospector", () -> new ProspectorEnchantment());
}

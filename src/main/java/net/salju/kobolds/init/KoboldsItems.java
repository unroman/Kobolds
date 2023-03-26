package net.salju.kobolds.init;

import net.salju.kobolds.item.KoboldPotionItem;
import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

public class KoboldsItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, KoboldsMod.MODID);
	public static final RegistryObject<Item> KOBOLD_SPAWN_EGG = REGISTRY.register("kobold_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD, -10066330, -6684775, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_WARRIOR_SPAWN_EGG = REGISTRY.register("kobold_warrior_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_WARRIOR, -10066330, -16738048, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_ENCHANTER_SPAWN_EGG = REGISTRY.register("kobold_enchanter_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_ENCHANTER, -10066330, -13057, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_ENGINEER_SPAWN_EGG = REGISTRY.register("kobold_engineer_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_ENGINEER, -10066330, -65536, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_PIRATE_SPAWN_EGG = REGISTRY.register("kobold_pirate_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_PIRATE, -10066330, -3355648, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_CAPTAIN_SPAWN_EGG = REGISTRY.register("kobold_captain_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_CAPTAIN, -6750208, -3355648, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_ZOMBIE_SPAWN_EGG = REGISTRY.register("kobold_zombie_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_ZOMBIE, -16724788, -6684775, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_ZOMBIE_DROWNED_SPAWN_EGG = REGISTRY.register("kobold_zombie_drowned_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_ZOMBIE_DROWNED, -16737895, -16737793, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_SKELETON_SPAWN_EGG = REGISTRY.register("kobold_skeleton_spawn_egg", () -> new ForgeSpawnEggItem(KoboldsModEntities.KOBOLD_SKELETON, -3355444, -13421773, new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_POTION_HEALTH = REGISTRY.register("kobold_potion_health", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_FIRE = REGISTRY.register("kobold_potion_fire", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_STEALTH = REGISTRY.register("kobold_potion_stealth", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_COMBAT = REGISTRY.register("kobold_potion_combat", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_WATER = REGISTRY.register("kobold_potion_water", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_LEAPING = REGISTRY.register("kobold_potion_leaping", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_LEVITATION = REGISTRY.register("kobold_potion_levitation", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_POTION_MINING = REGISTRY.register("kobold_potion_mining", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(16).rarity(Rarity.RARE)));
	public static final RegistryObject<Item> KOBOLD_SKULL = block(KoboldsBlocks.KOBOLD_SKULL, KoboldsTabs.KOBOLD_CREATIVE_TAB);
	public static final RegistryObject<Item> KOBOLD_INFINITY_POTION = REGISTRY.register("kobold_infinity_potion", () -> new KoboldPotionItem((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(1).rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> KOBOLD_IRON_SWORD = REGISTRY.register("kobold_iron_sword", () -> new SwordItem(KoboldsItemTiers.KOBOLD, 1, -2.2F, (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_IRON_SHOVEL = REGISTRY.register("kobold_iron_shovel", () -> new ShovelItem(KoboldsItemTiers.KOBOLD, 0.5F, -3.0F, (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_IRON_PICKAXE = REGISTRY.register("kobold_iron_pickaxe", () -> new PickaxeItem(KoboldsItemTiers.KOBOLD, 0, -2.8F, (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_IRON_AXE = REGISTRY.register("kobold_iron_axe", () -> new AxeItem(KoboldsItemTiers.KOBOLD, 4.0F, -3.0F, (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> KOBOLD_IRON_HOE = REGISTRY.register("kobold_iron_hoe", () -> new HoeItem(KoboldsItemTiers.KOBOLD, -3, -1.0F, (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));
	public static final RegistryObject<Item> BANNER_PATTERN_KOBOLD = REGISTRY.register("banner_pattern_kobold", () -> new BannerPatternItem(TagKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(KoboldsMod.MODID, "pattern_for_kobold")), (new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(1)));
	public static final RegistryObject<Item> MUSIC_DISC_KOBBLESTONE = REGISTRY.register("music_disc_kobblestone", () -> new RecordItem(0, () -> KoboldsModSounds.MUSIC_KOBBLESTONE.get(), new Item.Properties().tab(KoboldsTabs.KOBOLD_CREATIVE_TAB).stacksTo(1).rarity(Rarity.RARE), 3240));
	public static final RegistryObject<Item> KOBOLD_TEMPLATE = REGISTRY.register("kobold_template", () -> new Item((new Item.Properties()).tab(KoboldsTabs.KOBOLD_CREATIVE_TAB)));

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}

package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModSounds;
import net.salju.kobolds.init.KoboldsModEntities;
import net.salju.kobolds.init.KoboldsItems;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class KoboldRascalEntity extends AbstractKoboldEntity {
	public KoboldRascalEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_RASCAL.get(), world);
	}

	public KoboldRascalEntity(EntityType<KoboldRascalEntity> type, Level world) {
		super(type, world);
		this.setPersistenceRequired();
	}

	public boolean isFound;
	private int despawnDelay;

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!this.isFound && !(this.hasEffect(MobEffects.INVISIBILITY))) {
			this.isFound = true;
		}
		if (!this.level().isClientSide && --this.despawnDelay <= 0 && this.isFound) {
			this.discard();
		}
	}

	public void setDespawnDelay(int i) {
		this.despawnDelay = i;
	}

	public int getDespawnDelay() {
		return this.despawnDelay;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (!this.isFound) {
			if (!world.isClientSide()) {
				player.swing(hand, true);
				this.removeEffect(MobEffects.INVISIBILITY);
				this.swing(InteractionHand.MAIN_HAND, true);
				if (world instanceof ServerLevel lvl) {
					this.playSound(KoboldsModSounds.KOBOLD_TRADE.get(), 1.0F, 1.0F);
					ItemStack stack = new ItemStack(Items.BUNDLE);
					CompoundTag tag = stack.getOrCreateTag();
					tag.put("Items", new ListTag());
					ListTag list = tag.getList("Items", 10);
					if (Math.random() >= 0.85) {
						ItemStack loot = new ItemStack(KoboldsItems.KOBOLD_IRON_PICKAXE.get());
						loot.hurt(Mth.nextInt(RandomSource.create(), 28, 396), null, null);
						loot.enchant(Enchantments.BLOCK_EFFICIENCY, Mth.nextInt(RandomSource.create(), 1, 5));
						if (Math.random() >= 0.45) {
							loot.enchant(Enchantments.UNBREAKING, Mth.nextInt(RandomSource.create(), 1, 3));
						}
						if (Math.random() >= 0.65) {
							loot.enchant(Enchantments.BLOCK_FORTUNE, Mth.nextInt(RandomSource.create(), 1, 3));
						}
						CompoundTag loottag = new CompoundTag();
						loot.save(loottag);
						list.add(0, (Tag) loottag);
					} else {
						int max = Mth.nextInt(RandomSource.create(), 7, 38);
						for (int i = 0; i < max; ++i) {
							ItemStack loot = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation("kobolds:rascal_items"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.EMERALD)));
							if (Math.random() >= 0.99) {
								loot = new ItemStack(Items.DIAMOND);
							}
							Optional<CompoundTag> optional = this.getMatchingItem(loot, list);
							if (optional.isPresent()) {
								CompoundTag loottag = optional.get();
								ItemStack bagged = ItemStack.of(loottag);
								bagged.grow(loot.getCount());
								bagged.save(loottag);
								list.remove(loottag);
								list.add(0, (Tag) loottag);
							} else {
								CompoundTag loottag = new CompoundTag();
								loot.save(loottag);
								list.add(0, (Tag) loottag);
							}
						}
					}
					ItemEntity bundle = new ItemEntity(lvl, x, y, z, stack);
					bundle.setPickUpDelay(10);
					lvl.addFreshEntity(bundle);
				}
			}
			this.isFound = true;
		}
		for (AbstractKoboldEntity kobolds : this.level().getEntitiesOfClass(AbstractKoboldEntity.class, this.getBoundingBox().inflate(128.0D))) {
			if (!(kobolds instanceof KoboldRascalEntity)) {
				this.getNavigation().moveTo(kobolds.getX(), kobolds.getY(), kobolds.getZ(), 1.2);
			}
		}
		return InteractionResult.FAIL;
	}

	private static Optional<CompoundTag> getMatchingItem(ItemStack stack, ListTag tag) {
		return tag.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((loot) -> {
			return ItemStack.isSameItemSameTags(ItemStack.of(loot), stack);
		}).findFirst();
	}

	public static void init() {
	}

	@Override
	protected boolean canReplaceCurrentItem(ItemStack drop, ItemStack hand) {
		if (drop.getItem() instanceof SwordItem) {
			if (!(hand.getItem() instanceof SwordItem)) {
				return true;
			} else {
				SwordItem newbie = (SwordItem) drop.getItem();
				SwordItem weapon = (SwordItem) hand.getItem();
				if (newbie.getDamage() != weapon.getDamage()) {
					return newbie.getDamage() > weapon.getDamage();
				} else {
					return this.canReplaceEqualItem(drop, hand);
				}
			}
		} else if (drop.getItem() instanceof ArmorItem) {
			if (EnchantmentHelper.hasBindingCurse(hand)) {
				return false;
			} else if (hand.isEmpty() || hand.getItem() instanceof BlockItem) {
				return true;
			} else if (hand.getItem() instanceof ArmorItem) {
				ArmorItem newbie = (ArmorItem) drop.getItem();
				ArmorItem worn = (ArmorItem) hand.getItem();
				if (newbie.getDefense() != worn.getDefense()) {
					return newbie.getDefense() > worn.getDefense();
				} else if (newbie.getToughness() != worn.getToughness()) {
					return newbie.getToughness() > worn.getToughness();
				} else {
					return this.canReplaceEqualItem(drop, hand);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
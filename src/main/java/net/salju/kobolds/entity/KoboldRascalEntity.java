package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModSounds;
import net.salju.kobolds.init.KoboldsModEntities;

import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class KoboldRascalEntity extends AbstractKoboldEntity {
	public KoboldRascalEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_RASCAL.get(), world);
	}

	public KoboldRascalEntity(EntityType<KoboldRascalEntity> type, Level world) {
		super(type, world);
	}

	public boolean isFound;

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return true;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (!this.isFound && !(this.hasEffect(MobEffects.INVISIBILITY))) {
			this.isFound = true;
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		LevelAccessor world = this.level;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (!this.isFound) {
			if (!world.isClientSide()) {
				player.swing(hand, true);
				this.removeEffect(MobEffects.INVISIBILITY);
				if (Math.random() >= 0.92) {
					this.swing(InteractionHand.MAIN_HAND, true);
					if (world instanceof ServerLevel lvl) {
						this.playSound(KoboldsModSounds.KOBOLD_TRADE.get(), 1.0F, 1.0F);
						ItemEntity bundle = new ItemEntity(lvl, x, y, z, new ItemStack(Items.BUNDLE));
						bundle.setPickUpDelay(10);
						lvl.addFreshEntity(bundle);
					}
				}
			}
			this.isFound = true;
		}
		for (AbstractKoboldEntity kobolds : this.level.getEntitiesOfClass(AbstractKoboldEntity.class, this.getBoundingBox().inflate(128.0D))) {
			if (!(kobolds instanceof KoboldRascalEntity)) {
				this.getNavigation().moveTo(kobolds.getX(), kobolds.getY(), kobolds.getZ(), 1.2);
			}
		}
		return InteractionResult.FAIL;
	}

	public static void init() {
		SpawnPlacements.register(KoboldsModEntities.KOBOLD_RASCAL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (entityType, world, reason, pos, random) -> {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			return (!world.isClientSide() && world.isEmptyBlock(new BlockPos(x, y, z)) && world.getBlockState(new BlockPos(x, y, z)).getLightEmission(world, new BlockPos(x, y, z)) <= 4
					&& !world.getEntitiesOfClass(KoboldEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).isEmpty()
					&& !(!world.getEntitiesOfClass(KoboldRascalEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).isEmpty()));
		});
	}

	@Override
	protected boolean canReplaceCurrentItem(ItemStack item, ItemStack stack) {
		if (item.getItem() instanceof SwordItem) {
			if (stack.isEmpty() && (this.getOffhandItem().getItem() instanceof TridentItem)) {
				return false;
			} else if (!(stack.getItem() instanceof SwordItem)) {
				return true;
			} else {
				SwordItem sworditem = (SwordItem) item.getItem();
				SwordItem sworditem1 = (SwordItem) stack.getItem();
				if (sworditem.getDamage() != sworditem1.getDamage()) {
					return sworditem.getDamage() > sworditem1.getDamage();
				} else {
					return this.canReplaceEqualItem(item, stack);
				}
			}
		} else if (item.getItem() instanceof ArmorItem) {
			if (EnchantmentHelper.hasBindingCurse(stack)) {
				return false;
			} else if (!(stack.getItem() instanceof ArmorItem)) {
				return true;
			} else {
				ArmorItem armoritem = (ArmorItem) item.getItem();
				ArmorItem armoritem1 = (ArmorItem) stack.getItem();
				if (armoritem.getDefense() != armoritem1.getDefense()) {
					return armoritem.getDefense() > armoritem1.getDefense();
				} else if (armoritem.getToughness() != armoritem1.getToughness()) {
					return armoritem.getToughness() > armoritem1.getToughness();
				} else {
					return this.canReplaceEqualItem(item, stack);
				}
			}
		} else {
			return false;
		}
	}
}
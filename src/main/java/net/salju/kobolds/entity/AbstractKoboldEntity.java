package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModSounds;
import net.salju.kobolds.init.KoboldsModEntities;
import net.salju.kobolds.init.KoboldsItems;
import net.salju.kobolds.KoboldsMod;

import net.minecraftforge.event.ForgeEventFactory;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import javax.annotation.Nullable;

import java.util.function.Predicate;
import java.util.Iterator;

public abstract class AbstractKoboldEntity extends Monster implements CrossbowAttackMob, RangedAttackMob {
	private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE = SynchedEntityData.defineId(AbstractKoboldEntity.class, EntityDataSerializers.BOOLEAN);
	private ItemStack primary = new ItemStack(KoboldsItems.KOBOLD_IRON_SWORD.get());
	private ItemStack trident = ItemStack.EMPTY;
	private boolean partyKobold;
	@Nullable
	private BlockPos jukebox;

	protected AbstractKoboldEntity(EntityType<? extends Monster> type, Level world) {
		super(type, world);
		xpReward = 4;
		this.setCanPickUpLoot(true);
		((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(0, new AvoidEntityGoal(this, IronGolem.class, (float) 14, 1.2, 1.8));
		this.targetSelector.addGoal(0, new AbstractKoboldEntity.KoboldRevengeGoal(this));
		this.goalSelector.addGoal(1, new AbstractKoboldEntity.KoboldShieldGoal(this));
		this.goalSelector.addGoal(1, new AbstractKoboldEntity.KoboldHealGoal(this));
		this.goalSelector.addGoal(1, new AbstractKoboldEntity.KoboldTridentAttackGoal(this, 1.0D, 40, 12.0F));
		this.goalSelector.addGoal(1, new RangedCrossbowAttackGoal<>(this, 1.0D, 12.0F));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 12, true, false, new AbstractKoboldEntity.KoboldAttackSelector(this)));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, LivingEntity.class, (float) 6));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(3, new FloatGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.MAX_HEALTH, 18);
		builder = builder.add(Attributes.ARMOR, 2);
		return builder;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Primary", this.primary.save(new CompoundTag()));
		tag.put("Trident", this.trident.save(new CompoundTag()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Primary")) {
			ItemStack stack = ItemStack.of(tag.getCompound("Primary"));
			this.primary = stack;
		}
		if (tag.contains("Trident")) {
			ItemStack stack = ItemStack.of(tag.getCompound("Trident"));
			this.trident = stack;
		}
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean isPreventingPlayerRest(Player player) {
		return false;
	}

	@Override
	public double getMyRidingOffset() {
		return this.isBaby() ? 0.0D : -0.225D;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !(this.isPersistenceRequired());
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
			this.performCrossbowAttack(this, 6.0F);
		} else if (this.getOffhandItem().getItem() instanceof TridentItem) {
			this.trident = this.getOffhandItem();
			ThrownTrident proj = new ThrownTrident(this.level(), this, this.trident);
			double d0 = target.getX() - this.getX();
			double d1 = target.getY(0.3333333333333333D) - proj.getY();
			double d2 = target.getZ() - this.getZ();
			double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
			proj.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
			this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
			this.level().addFreshEntity(proj);
			this.setItemInHand(InteractionHand.MAIN_HAND, this.primary);
			this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
			this.getPersistentData().putDouble("TimerCooldown", 1200);
		}
	}

	@Override
	public void onCrossbowAttackPerformed() {
		this.noActionTime = 0;
	}

	@Override
	public void shootCrossbowProjectile(LivingEntity arg0, ItemStack arg1, Projectile arg2, float arg3) {
		this.shootCrossbowProjectile(this, arg0, arg2, arg3, 1.6F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_CHARGING_STATE, false);
	}

	public boolean isCharging() {
		return this.entityData.get(DATA_CHARGING_STATE);
	}

	public void setChargingCrossbow(boolean charging) {
		this.entityData.set(DATA_CHARGING_STATE, charging);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public ItemStack equipItemIfPossible(ItemStack stack) {
		EquipmentSlot slot = getEquipmentSlotForItem(stack);
		ItemStack current = this.getItemBySlot(slot);
		boolean flag = this.canReplaceCurrentItem(stack, current);
		if (stack.getItem() == Items.EMERALD || stack.getItem() instanceof TridentItem) {
			slot = EquipmentSlot.OFFHAND;
			current = this.getItemBySlot(slot);
			flag = this.canReplaceCurrentItem(stack, current);
		}
		if (flag && this.canHoldItem(stack)) {
			double d0 = (double) this.getEquipmentDropChance(slot);
			if (!current.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
				this.spawnAtLocation(current);
			}
			if (stack.getItem() == Items.EMERALD && (stack.getCount() > 1)) {
				stack.setCount(1);
			}
			if (slot.isArmor() && stack.getCount() > 1) {
				ItemStack copy = stack.copyWithCount(1);
				this.setItemSlotAndDropWhenKilled(slot, copy);
				return copy;
			} else {
				this.setItemSlotAndDropWhenKilled(slot, stack);
				return stack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	protected boolean canReplaceCurrentItem(ItemStack drop, ItemStack hand) {
		if (drop.getItem() instanceof SwordItem) {
			if (hand.isEmpty() && (this.getOffhandItem().getItem() instanceof TridentItem)) {
				return false;
			} else if (!(hand.getItem() instanceof SwordItem)) {
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
		} else if (drop.getItem() instanceof CrossbowItem && hand.getItem() instanceof CrossbowItem) {
			return this.canReplaceEqualItem(drop, hand);
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
		} else if (drop.getItem() instanceof ShieldItem && hand.isEmpty() && this.trident.isEmpty()) {
			return true;
		} else if (drop.getItem() instanceof TridentItem && hand.isEmpty() && this.trident.isEmpty()) {
			this.primary = this.getMainHandItem();
			this.trident = drop;
			this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			return true;
		} else if (drop.getItem() == Items.EMERALD && hand.isEmpty() && !(this instanceof KoboldCaptainEntity)) {
			return true;
		} else {
			return false;
		}
	}

	public void aiStep() {
		if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 12.76D) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
			this.partyKobold = false;
			this.jukebox = null;
		}
		super.aiStep();
	}

	public void setRecordPlayingNearby(BlockPos pos, boolean boop) {
		this.jukebox = pos;
		this.partyKobold = boop;
	}

	public boolean isPartyKobold() {
		return this.partyKobold;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return KoboldsModSounds.KOBOLD_IDLE.get();
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		if (this.isBlocking()) {
			return SoundEvents.SHIELD_BLOCK;
		} else {
			return KoboldsModSounds.KOBOLD_HURT.get();
		}
	}

	@Override
	public SoundEvent getDeathSound() {
		return KoboldsModSounds.KOBOLD_DEATH.get();
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (source.getDirectEntity() instanceof Zombie) {
			if (!this.isBaby() && this.level().getDifficulty() == Difficulty.HARD || this.level().getDifficulty() == Difficulty.NORMAL) {
				this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
				if (this.level() instanceof ServerLevel lvl) {
					KoboldZombieEntity zombo = this.convertTo(KoboldsModEntities.KOBOLD_ZOMBIE.get(), true);
					ForgeEventFactory.onLivingConvert(this, zombo);
				}
			}
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		ItemStack gem = (player.getItemInHand(hand).copy());
		ItemStack weapon = this.getMainHandItem();
		ItemStack off = this.getOffhandItem();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (this.isAlive()) {
			if (player.getMainHandItem().getItem() == (ItemStack.EMPTY).getItem() && player.getOffhandItem().getItem() == (ItemStack.EMPTY).getItem()) {
				this.playSound(KoboldsModSounds.KOBOLD_PURR.get(), 1.0F, 1.0F);
				if (player instanceof ServerPlayer ply) {
					Advancement adv = ply.server.getAdvancements().getAdvancement(new ResourceLocation("kobolds:kobold_pet_advancement"));
					AdvancementProgress ap = ply.getAdvancements().getOrStartProgress(adv);
					if (!ap.isDone()) {
						Iterator gator = ap.getRemainingCriteria().iterator();
						while (gator.hasNext())
							ply.getAdvancements().award(adv, (String) gator.next());
					}
				}
				player.swing(InteractionHand.MAIN_HAND);
			} else if (!world.isClientSide() && off.getItem() == (ItemStack.EMPTY).getItem() && this.isEffectiveAi()) {
				if (gem.getItem() instanceof AxeItem) {
					if (this instanceof KoboldEntity) {
						this.setItemInHand(InteractionHand.OFF_HAND, gem);
						if (!player.getAbilities().instabuild) {
							(player.getItemInHand(hand)).shrink(1);
						}
						return InteractionResult.SUCCESS;
					}
				} else if (gem.getItem() == Items.EMERALD) {
					if (this instanceof KoboldEntity || this instanceof KoboldPirateEntity || this instanceof KoboldEnchanterEntity || this instanceof KoboldEngineerEntity) {
						this.setItemInHand(InteractionHand.OFF_HAND, gem);
						if (!player.getAbilities().instabuild) {
							(player.getItemInHand(hand)).shrink(1);
						}
						return InteractionResult.SUCCESS;
					}
				} else if (gem.is(ItemTags.create(new ResourceLocation("kobolds:kobold_breed_items")))) {
					if (!(this instanceof KoboldWarriorEntity) && !this.isBaby() && !world.getEntitiesOfClass(KoboldWarriorEntity.class, AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).isEmpty()
							&& this.getPersistentData().getDouble("TimerApple") <= 0) {
						this.getPersistentData().putDouble("TimerApple", 24000);
						if (!player.getAbilities().instabuild) {
							(player.getItemInHand(hand)).shrink(1);
						}
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		RandomSource randy = world.getRandom();
		this.populateDefaultEquipmentSlots(randy, difficulty);
		if (this instanceof KoboldRascalEntity) {
			this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 24000, 0));
		}
		return retval;
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randy, DifficultyInstance souls) {
		ItemStack sword = (EnchantmentHelper.enchantItem(RandomSource.create(), new ItemStack(KoboldsItems.KOBOLD_IRON_SWORD.get()), 21, false));
		ItemStack crossbow = (EnchantmentHelper.enchantItem(RandomSource.create(), new ItemStack(Items.CROSSBOW), 21, false));
		if (this instanceof KoboldWarriorEntity) {
			this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(KoboldsItems.KOBOLD_IRON_AXE.get()));
		} else if (this instanceof KoboldRascalEntity || this instanceof KoboldCaptainEntity) {
			this.setItemSlot(EquipmentSlot.MAINHAND, sword);
		} else if (this instanceof KoboldEngineerEntity) {
			this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
		} else if (this instanceof KoboldEntity || this instanceof KoboldPirateEntity) {
			if (Math.random() >= 0.6) {
				if (Math.random() >= 0.15) {
					this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
				} else {
					this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
				}
			} else {
				if (Math.random() >= 0.15) {
					this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(KoboldsItems.KOBOLD_IRON_SWORD.get()));
				} else {
					this.setItemSlot(EquipmentSlot.MAINHAND, sword);
				}
			}
		}
		this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
		if (this instanceof KoboldWarriorEntity) {
			this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
		} else if (this instanceof KoboldPirateEntity) {
			if (Math.random() >= 0.75) {
				this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TRIDENT));
				this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				this.trident = this.getOffhandItem();
			}
		}
		this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (!world.isClientSide() && this.isAlive() && this.isEffectiveAi()) {
			if (this.getPersistentData().getDouble("TimerCooldown") > 0) {
				if (this.getPersistentData().getDouble("TimerCooldown") == 1 && !(this.trident.isEmpty())) {
					this.primary = this.getMainHandItem();
					this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					this.setItemInHand(InteractionHand.OFF_HAND, this.trident);
				}
				this.getPersistentData().putDouble("TimerCooldown", (this.getPersistentData().getDouble("TimerCooldown") - 1));
			}
			if (this.getPersistentData().getDouble("TimerApple") > 0) {
				if (this.getPersistentData().getDouble("TimerApple") == 12000) {
					if (world instanceof ServerLevel lvl) {
						Mob kobold = new KoboldChildEntity(KoboldsModEntities.KOBOLD_CHILD.get(), lvl);
						kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
						kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						world.addFreshEntity(kobold);
					}
				}
				this.getPersistentData().putDouble("TimerApple", (this.getPersistentData().getDouble("TimerApple") - 1));
			}
			for (ThrownTrident proj : this.level().getEntitiesOfClass(ThrownTrident.class, this.getBoundingBox().inflate(0.85D))) {
				if ((proj.getOwner() == this) && (proj.clientSideReturnTridentTickCount > 0) && this.getOffhandItem().isEmpty()) {
					this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					this.setItemInHand(InteractionHand.OFF_HAND, this.trident);
					this.getPersistentData().putDouble("TimerCooldown", 0);
					proj.discard();
				}
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		Entity direct = source.getDirectEntity();
		if ((direct instanceof Projectile proj && proj.getOwner() instanceof AbstractKoboldEntity) || (direct instanceof AbstractKoboldEntity) || (source.is(DamageTypes.IN_FIRE))) {
			return false;
		}
		if (direct instanceof LivingEntity target && target.canDisableShield() && this.isBlocking()) {
			this.getPersistentData().putDouble("TimerCooldown", 100);
		}
		return super.hurt(source, amount);
	}

	class KoboldRevengeGoal extends HurtByTargetGoal {
		public KoboldRevengeGoal(AbstractKoboldEntity kobold) {
			super(kobold);
			this.setAlertOthers(new Class[]{AbstractKoboldEntity.class});
		}

		@Override
		public void start() {
			super.start();
			for (AbstractKoboldEntity kobolds : this.mob.level().getEntitiesOfClass(AbstractKoboldEntity.class, this.mob.getBoundingBox().inflate(32.0D), (kobold) -> kobold.getTarget() == null)) {
				if (!(this.mob.level().getDifficulty() == Difficulty.PEACEFUL)) {
					kobolds.setTarget(this.mob.getTarget());
				}
			}
		}
	}

	class KoboldShieldGoal extends Goal {
		public final AbstractKoboldEntity kobold;

		public KoboldShieldGoal(AbstractKoboldEntity kobold) {
			this.kobold = kobold;
		}

		@Override
		public boolean canUse() {
			return kobold.getOffhandItem().getItem() instanceof ShieldItem && raiseShield() && (kobold.getPersistentData().getDouble("TimerCooldown") == 0);
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		@Override
		public void start() {
			kobold.startUsingItem(InteractionHand.OFF_HAND);
		}

		@Override
		public void stop() {
			kobold.stopUsingItem();
		}

		protected boolean raiseShield() {
			if (kobold.getTarget() != null) {
				LivingEntity target = kobold.getTarget();
				if (target instanceof RangedAttackMob && kobold.distanceTo(target) >= 0.2D) {
					return true;
				} else if (kobold.distanceTo(target) >= 0.2D && kobold.distanceTo(target) <= 5.2D) {
					return true;
				}
			}
			return false;
		}
	}

	class KoboldHealGoal extends Goal {
		public final AbstractKoboldEntity kobold;

		public KoboldHealGoal(AbstractKoboldEntity kobold) {
			this.kobold = kobold;
		}

		@Override
		public boolean canUse() {
			return (kobold.getHealth() < 12) && (kobold.getPersistentData().getDouble("TimerCooldown") == 0) && checkHand() && !kobold.isBaby() && !(kobold instanceof KoboldRascalEntity);
		}

		@Override
		public void start() {
			kobold.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(KoboldsItems.KOBOLD_POTION_HEALTH.get()));
			kobold.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 32, -4, (false), (false)));
			kobold.playSound(SoundEvents.GENERIC_DRINK, 0.5F, 1.0F);
			KoboldsMod.queueServerWork(32, () -> {
				kobold.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1, (false), (false)));
				kobold.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0, (false), (false)));
				kobold.getPersistentData().putDouble("TimerCooldown", 600);
				kobold.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
			});
		}

		protected boolean checkHand() {
			return (kobold.getOffhandItem().isEmpty()) || (kobold.getOffhandItem().getItem() == KoboldsItems.KOBOLD_POTION_HEALTH.get());
		}
	}

	class KoboldTridentAttackGoal extends RangedAttackGoal {
		public final AbstractKoboldEntity kobold;

		public KoboldTridentAttackGoal(RangedAttackMob kobold, double dub, int inty, float enty) {
			super(kobold, dub, inty, enty);
			this.kobold = (AbstractKoboldEntity) kobold;
		}

		public boolean canUse() {
			return (super.canUse() && kobold.getOffhandItem().getItem() == Items.TRIDENT);
		}

		public void start() {
			super.start();
			kobold.setAggressive(true);
			kobold.startUsingItem(InteractionHand.OFF_HAND);
		}

		public void stop() {
			super.stop();
			kobold.setAggressive(false);
			kobold.stopUsingItem();
		}
	}

	static class KoboldAttackSelector implements Predicate<LivingEntity> {
		private final AbstractKoboldEntity kobold;

		public KoboldAttackSelector(AbstractKoboldEntity source) {
			this.kobold = source;
		}

		public boolean test(@Nullable LivingEntity target) {
			if (!(kobold instanceof KoboldEnchanterEntity && kobold instanceof KoboldChildEntity && target instanceof ZombifiedPiglin)) {
				if (kobold instanceof KoboldWarriorEntity) {
					return (target instanceof Zombie || target instanceof Skeleton || target instanceof Spider || target instanceof Villager || target instanceof Raider);
				} else if (kobold instanceof KoboldEngineerEntity || kobold instanceof KoboldRascalEntity) {
					return (target instanceof Villager || target instanceof Raider);
				} else {
					return (target instanceof Zombie || target instanceof Villager || target instanceof Silverfish);
				}
			}
			return false;
		}
	}
}

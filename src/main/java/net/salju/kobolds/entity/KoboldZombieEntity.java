package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModEntities;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.event.ForgeEventFactory;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Difficulty;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class KoboldZombieEntity extends Zombie {
	private static final EntityDataAccessor<Boolean> DATA_CONVERTING = SynchedEntityData.defineId(KoboldZombieEntity.class, EntityDataSerializers.BOOLEAN);

	public KoboldZombieEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_ZOMBIE.get(), world);
	}

	public KoboldZombieEntity(EntityType<KoboldZombieEntity> type, Level world) {
		super(type, world);
		getEyePosition(0.5F);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_CONVERTING, false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
	}

	@Override
	public double getMyRidingOffset() {
		return this.isBaby() ? 0.0D : -0.225D;
	}

	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return this.isBaby() ? 0.66F : 1.26F;
	}

	protected boolean convertsInWater() {
		return false;
	}

	public boolean isBaby() {
		return false;
	}

	public boolean isConvert() {
		return this.getEntityData().get(DATA_CONVERTING);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie_villager.death"));
	}

	public static void init() {
		SpawnPlacements.register(KoboldsModEntities.KOBOLD_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (!world.isClientSide() && this.isAlive() && !this.isNoAi()) {
			if (this.getPersistentData().getDouble("Drown") < 600) {
				if (this.isInWater() && this.isEyeInFluid(FluidTags.WATER)) {
					this.getPersistentData().putDouble("Drown", (this.getPersistentData().getDouble("Drown") + 1));
				} else if (this.getPersistentData().getDouble("Drown") > 0) {
					this.getPersistentData().putDouble("Drown", (this.getPersistentData().getDouble("Drown") - 1));
				}
			} else if (this.getPersistentData().getDouble("Drown") >= 600) {
				this.playSound(SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, 1.0F, 1.0F);
				if (world instanceof ServerLevel lvl) {
					KoboldZombieDrownedEntity drown = this.convertTo(KoboldsModEntities.KOBOLD_ZOMBIE_DROWNED.get(), true);
					ForgeEventFactory.onLivingConvert(this, drown);
				}
			}
			if (this.getPersistentData().getDouble("Convert") > 1) {
				this.getPersistentData().putDouble("Convert", (this.getPersistentData().getDouble("Convert") - 1));
			} else if (this.getPersistentData().getDouble("Convert") == 1) {
				if (this.isAlive()) {
					this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
					ItemStack weapon = this.getMainHandItem();
					ItemStack off = this.getOffhandItem();
					if (world instanceof ServerLevel lvl) {
						if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_jungle")))) {
							if (weapon.getItem() instanceof SwordItem && weapon.isEnchanted()) {
								KoboldCaptainEntity capty = this.convertTo(KoboldsModEntities.KOBOLD_CAPTAIN.get(), true);
								ForgeEventFactory.onLivingConvert(this, capty);
							} else {
								KoboldPirateEntity pirate = this.convertTo(KoboldsModEntities.KOBOLD_PIRATE.get(), true);
								ForgeEventFactory.onLivingConvert(this, pirate);
							}
						} else if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_mountain"))) && weapon.getItem() == Items.CROSSBOW && off == (ItemStack.EMPTY)) {
							KoboldEngineerEntity engi = this.convertTo(KoboldsModEntities.KOBOLD_ENGINEER.get(), true);
							ForgeEventFactory.onLivingConvert(this, engi);
						} else if (off.getItem() instanceof ShieldItem) {
							KoboldWarriorEntity war = this.convertTo(KoboldsModEntities.KOBOLD_WARRIOR.get(), true);
							ForgeEventFactory.onLivingConvert(this, war);
						} else {
							KoboldEntity basic = this.convertTo(KoboldsModEntities.KOBOLD.get(), true);
							ForgeEventFactory.onLivingConvert(this, basic);
						}
					}
				}
			}
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		ItemStack apple = player.getItemInHand(hand);
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		double waitTicks = 0;
		double potionLevel = 0;
		if (!world.isClientSide() && apple.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
			if (world.getDifficulty() == Difficulty.EASY) {
				potionLevel = 0;
				waitTicks = 1200;
			} else if (world.getDifficulty() == Difficulty.NORMAL) {
				potionLevel = 0;
				waitTicks = 2400;
			} else if (world.getDifficulty() == Difficulty.HARD) {
				potionLevel = 1;
				waitTicks = 4800;
			}
			if (!player.getAbilities().instabuild) {
				(apple).shrink(1);
			}
			player.swing(hand, true);
			this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
			this.removeEffect(MobEffects.WEAKNESS);
			this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) waitTicks, (int) potionLevel));
			this.getPersistentData().putDouble("Convert", waitTicks);
			this.getEntityData().set(DATA_CONVERTING, true);
		}
		return InteractionResult.FAIL;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 18);
		builder = builder.add(Attributes.ARMOR, 2);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
		return builder;
	}
}

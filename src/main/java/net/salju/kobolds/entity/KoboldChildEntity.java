package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModEntities;

import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class KoboldChildEntity extends AbstractKoboldEntity {
	public KoboldChildEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_CHILD.get(), world);
	}

	public KoboldChildEntity(EntityType<KoboldChildEntity> type, Level world) {
		super(type, world);
		this.setPersistenceRequired();
		this.setCanPickUpLoot(false);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new PanicGoal(this, 1.2));
	}

	public boolean isBaby() {
		return true;
	}

	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return this.isBaby() ? 0.66F : 1.26F;
	}

	public static void init() {
	}

	@Override
	public void baseTick() {
		super.baseTick();
		LevelAccessor world = this.level;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (!world.isClientSide()) {
			if (this.getPersistentData().getDouble("TimerGrow") < 24000 && (this.getDisplayName().getString()).equals(Component.translatable("entity.kobolds.kobold_child").getString())) {
				this.getPersistentData().putDouble("TimerGrow", (this.getPersistentData().getDouble("TimerGrow") + 1));
			} else if (this.getPersistentData().getDouble("TimerGrow") >= 24000) {
				this.discard();
				if (world instanceof ServerLevel lvl) {
					if (world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_jungle")))) {
						if (Math.random() < 0.06) {
							Mob kobold = new KoboldCaptainEntity(KoboldsModEntities.KOBOLD_CAPTAIN.get(), lvl);
							kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
							kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
							world.addFreshEntity(kobold);
						} else {
							Mob kobold = new KoboldPirateEntity(KoboldsModEntities.KOBOLD_PIRATE.get(), lvl);
							kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
							kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
							world.addFreshEntity(kobold);
						}
					} else if (Math.random() > 0.95) {
						Mob kobold = new KoboldEngineerEntity(KoboldsModEntities.KOBOLD_ENGINEER.get(), lvl);
						kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
						kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						world.addFreshEntity(kobold);
					} else if (Math.random() < 0.1) {
						Mob kobold = new KoboldEnchanterEntity(KoboldsModEntities.KOBOLD_ENCHANTER.get(), lvl);
						kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
						kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						world.addFreshEntity(kobold);
					} else {
						Mob kobold = new KoboldEntity(KoboldsModEntities.KOBOLD.get(), lvl);
						kobold.moveTo(x, y, z, world.getRandom().nextFloat() * 360F, 0);
						kobold.finalizeSpawn(lvl, world.getCurrentDifficultyAt(kobold.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						world.addFreshEntity(kobold);
					}
				}
			}
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		LevelAccessor world = this.level;
		ItemStack gem = player.getItemInHand(hand);
		if (gem.is(ItemTags.create(new ResourceLocation("kobolds:kobold_breed_items")))) {
			if (!player.getAbilities().instabuild) {
				(gem).shrink(1);
			}
			player.swing(hand, true);
			this.getPersistentData().putDouble("TimerGrow", (this.getPersistentData().getDouble("TimerGrow") + 1256));
		}
		return InteractionResult.FAIL;
	}
}

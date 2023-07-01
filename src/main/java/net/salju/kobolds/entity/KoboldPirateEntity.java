package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModSounds;
import net.salju.kobolds.init.KoboldsModEntities;
import net.salju.kobolds.KoboldsMod;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class KoboldPirateEntity extends AbstractKoboldEntity {
	public KoboldPirateEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_PIRATE.get(), world);
	}

	public KoboldPirateEntity(EntityType<KoboldPirateEntity> type, Level world) {
		super(type, world);
		this.setPersistenceRequired();
	}

	public static void init() {
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new KoboldPirateEntity.KoboldTradeGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
	}

	class KoboldTradeGoal extends Goal {
		public final AbstractKoboldEntity kobold;

		public KoboldTradeGoal(AbstractKoboldEntity kobold) {
			this.kobold = kobold;
		}

		@Override
		public boolean canUse() {
			return (checkHand() && !(this.kobold.hasEffect(MobEffects.MOVEMENT_SPEED)));
		}

		@Override
		public void start() {
			this.kobold.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
			KoboldsMod.queueServerWork(100, () -> {
				this.kobold.swing(InteractionHand.MAIN_HAND, true);
				this.kobold.playSound(KoboldsModSounds.KOBOLD_TRADE.get(), 1.0F, 1.0F);
				LevelAccessor world = this.kobold.level();
				double x = this.kobold.getX();
				double y = this.kobold.getY();
				double z = this.kobold.getZ();
				if (world instanceof ServerLevel lvl) {
					lvl.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, lvl, 4, "", Component.literal(""), lvl.getServer(), null).withSuppressedOutput(),
							"/loot spawn ~ ~ ~ loot kobolds:gameplay/trader_loot");
				}
				KoboldsMod.queueServerWork(20, () -> {
					this.kobold.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
				});
			});
		}

		protected boolean checkHand() {
			return (this.kobold.getOffhandItem().getItem() == (Items.EMERALD));
		}
	}
}
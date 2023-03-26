package net.salju.kobolds.item;

import net.salju.kobolds.init.KoboldsItems;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class KoboldPotionItem extends Item {
	public KoboldPotionItem(Item.Properties props) {
		super(props);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		if (stack.getItem() == KoboldsItems.KOBOLD_POTION_HEALTH.get() || stack.getItem() == KoboldsItems.KOBOLD_INFINITY_POTION.get()) {
			list.add(Component.translatable("desc.kobolds.potion_healing").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_FIRE.get()) {
			list.add(Component.translatable("desc.kobolds.potion_fire").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_STEALTH.get()) {
			list.add(Component.translatable("desc.kobolds.potion_stealth").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_COMBAT.get()) {
			list.add(Component.translatable("desc.kobolds.potion_combat").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_WATER.get()) {
			list.add(Component.translatable("desc.kobolds.potion_water").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_LEAPING.get()) {
			list.add(Component.translatable("desc.kobolds.potion_leap").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_LEVITATION.get()) {
			list.add(Component.translatable("desc.kobolds.potion_float").withStyle(ChatFormatting.BLUE));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_MINING.get()) {
			list.add(Component.translatable("desc.kobolds.potion_haste").withStyle(ChatFormatting.BLUE));
		}
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(lvl, player, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
		ItemStack empty = new ItemStack(Items.GLASS_BOTTLE);
		super.finishUsingItem(stack, world, entity);
		if (stack.getItem() == KoboldsItems.KOBOLD_POTION_HEALTH.get() || stack.getItem() == KoboldsItems.KOBOLD_INFINITY_POTION.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1, (false), (false)));
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 240, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_FIRE.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2700, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_STEALTH.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2700, 0));
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2700, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_COMBAT.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2700, 0));
			entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2700, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_WATER.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 2700, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_LEAPING.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 0));
			entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_LEVITATION.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 240, 0));
		} else if (stack.getItem() == KoboldsItems.KOBOLD_POTION_MINING.get()) {
			entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 0));
		}
		if (stack.isEmpty()) {
			return empty;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (stack.getItem() == KoboldsItems.KOBOLD_INFINITY_POTION.get()) {
					player.getCooldowns().addCooldown(stack.getItem(), 240);
				} else {
					(stack).shrink(1);
					if (!player.getInventory().add(empty))
						player.drop(empty, false);
				}
			}
			return stack;
		}
	}
}
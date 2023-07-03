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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import javax.annotation.Nullable;
import java.util.List;

public class KoboldPotionItem extends Item {
	private MobEffect primary;
	private MobEffect secondary;
	private int duration;
	private String desc;

	public KoboldPotionItem(Item.Properties props, MobEffect first, @Nullable MobEffect second, int dur, String string) {
		super(props);
		this.primary = first;
		if (second != null) {
			this.secondary = second;
		}
		this.duration = dur;
		this.desc = string;
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
		list.add(Component.translatable(this.desc).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity target) {
		ItemStack empty = new ItemStack(Items.GLASS_BOTTLE);
		super.finishUsingItem(stack, world, target);
		if (this.primary.isInstantenous()) {
			target.addEffect(new MobEffectInstance(this.primary, 1, 0, false, false));
		} else {
			target.addEffect(new MobEffectInstance(this.primary, this.duration, 0));
		}
		if (this.secondary != null) {
			if (this.secondary.isInstantenous()) {
				target.addEffect(new MobEffectInstance(this.secondary, 1, 0, false, false));
			} else {
				target.addEffect(new MobEffectInstance(this.secondary, this.duration, 0));
			}
		}
		if (stack.isEmpty()) {
			return empty;
		} else {
			if (target instanceof Player player && !player.getAbilities().instabuild) {
				if (stack.getItem() == KoboldsItems.KOBOLD_INFINITY_POTION.get()) {
					player.getCooldowns().addCooldown(stack.getItem(), 240);
				} else {
					stack.shrink(1);
					if (!player.getInventory().add(empty))
						player.drop(empty, false);
				}
			}
			return stack;
		}
	}
}
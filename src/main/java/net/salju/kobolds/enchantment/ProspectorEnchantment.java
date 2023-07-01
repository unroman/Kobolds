package net.salju.kobolds.enchantment;

import net.salju.kobolds.init.KoboldsEnchantments;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

public class ProspectorEnchantment extends Enchantment {
	public ProspectorEnchantment(EquipmentSlot... slots) {
		super(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.DIGGER, slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	protected boolean checkCompatibility(Enchantment ench) {
		return !(ench == Enchantments.MOB_LOOTING || ench == KoboldsEnchantments.PROSPECTOR.get());
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return (stack.getItem() instanceof AxeItem);
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public void doPostAttack(LivingEntity source, Entity target, int inty) {
		if (target instanceof Monster) {
			ItemStack weapon = source.getMainHandItem();
			LevelAccessor world = target.level();
			double x = target.getX();
			double y = target.getY();
			double z = target.getZ();
			if (!target.isAlive() && !world.isClientSide()) {
				if (source.hasEffect(MobEffects.LUCK) && Math.random() <= 0.3) {
					for (int index0 = 0; index0 < (int) (1 * EnchantmentHelper.getItemEnchantmentLevel(KoboldsEnchantments.PROSPECTOR.get(), weapon)); index0++) {
						ItemEntity gem = new ItemEntity(target.level(), x, y, z, new ItemStack(Items.EMERALD));
						gem.setPickUpDelay(10);
						target.level().addFreshEntity(gem);
					}
				} else if (Math.random() <= 0.1) {
					for (int index0 = 0; index0 < (int) (1 * EnchantmentHelper.getItemEnchantmentLevel(KoboldsEnchantments.PROSPECTOR.get(), weapon)); index0++) {
						ItemEntity gem = new ItemEntity(target.level(), x, y, z, new ItemStack(Items.EMERALD));
						gem.setPickUpDelay(10);
						target.level().addFreshEntity(gem);
					}
				}
			}
		}
	}
}
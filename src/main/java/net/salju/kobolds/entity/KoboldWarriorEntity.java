package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModEntities;

import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.EntityType;

public class KoboldWarriorEntity extends AbstractKoboldEntity {
	public KoboldWarriorEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(KoboldsModEntities.KOBOLD_WARRIOR.get(), world);
	}

	public KoboldWarriorEntity(EntityType<KoboldWarriorEntity> type, Level world) {
		super(type, world);
		this.setPersistenceRequired();
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
	}

	public static void init() {
	}

	@Override
	protected boolean canReplaceCurrentItem(ItemStack item, ItemStack stack) {
		if (item.getItem() instanceof AxeItem) {
			if (stack.isEmpty() && (this.getOffhandItem().getItem() instanceof TridentItem)) {
				return false;
			} else if (!(stack.getItem() instanceof AxeItem)) {
				return true;
			} else {
				AxeItem sworditem = (AxeItem) item.getItem();
				AxeItem sworditem1 = (AxeItem) stack.getItem();
				if (sworditem.getAttackDamage() != sworditem1.getAttackDamage()) {
					return sworditem.getAttackDamage() > sworditem1.getAttackDamage();
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
		} else if (item.getItem() instanceof ShieldItem && stack.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
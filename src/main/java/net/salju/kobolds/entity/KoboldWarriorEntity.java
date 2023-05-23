package net.salju.kobolds.entity;

import net.salju.kobolds.init.KoboldsModEntities;
import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
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
	protected boolean canReplaceCurrentItem(ItemStack drop, ItemStack hand) {
		if (drop.getItem() instanceof AxeItem) {
			if (hand.isEmpty() && (this.getOffhandItem().getItem() instanceof TridentItem)) {
				return false;
			} else if (!(hand.getItem() instanceof AxeItem)) {
				return true;
			} else {
				AxeItem newbie = (AxeItem) drop.getItem();
				AxeItem weapon = (AxeItem) hand.getItem();
				if (newbie.getAttackDamage() != weapon.getAttackDamage()) {
					return newbie.getAttackDamage() > weapon.getAttackDamage();
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
		} else if (drop.getItem() instanceof ShieldItem && hand.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
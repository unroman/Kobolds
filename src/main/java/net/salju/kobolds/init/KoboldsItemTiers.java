package net.salju.kobolds.init;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public enum KoboldsItemTiers implements Tier {
	KOBOLD(2, 0, 4.0F, 3.0F, 14, () -> Ingredient.of(Items.IRON_INGOT));

	private final int level;
	private final int durability;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	KoboldsItemTiers(int level, int durability, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
		this.level = level;
		this.durability = durability;
		this.speed = speed;
		this.damage = damage;
		this.enchantmentValue = enchantmentValue;
		this.repairIngredient = repairIngredient;
	}

	public int getUses() {
		return this.durability;
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getAttackDamageBonus() {
		return this.damage;
	}

	public int getLevel() {
		return this.level;
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public @NotNull Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}
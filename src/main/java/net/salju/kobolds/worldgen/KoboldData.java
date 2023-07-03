package net.salju.kobolds.worldgen;

import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.HashMap;

public class KoboldData extends SavedData {
	private static final String IDENTIFIER = "kobold_data";
	private static Map<Level, KoboldData> dataMap = new HashMap<>();
	private int rascalDelay;
	private int rascalChance;

	public KoboldData() {
		super();
	}

	public static KoboldData get(Level world) {
		if (world instanceof ServerLevel) {
			ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
			KoboldData map = dataMap.get(overworld);
			if (map == null) {
				DimensionDataStorage storage = overworld.getDataStorage();
				KoboldData data = storage.computeIfAbsent(KoboldData::load, KoboldData::new, IDENTIFIER);
				if (data != null) {
					data.setDirty();
				}
				dataMap.put(world, data);
				return data;
			}
			return map;
		}
		return null;
	}

	public static KoboldData load(CompoundTag tag) {
		KoboldData data = new KoboldData();
		if (tag.contains("RascalDelay", 99)) {
			data.rascalDelay = tag.getInt("RascalDelay");
		}
		if (tag.contains("RascalChance", 99)) {
			data.rascalChance = tag.getInt("RascalChance");
		}
		return data;
	}

	public int getRascalDelay() {
		return this.rascalDelay;
	}

	public void setRascalDelay(int i) {
		this.rascalDelay = i;
	}

	public int getRascalChance() {
		return this.rascalChance;
	}

	public void setRascalChance(int i) {
		this.rascalChance = i;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putInt("RascalDelay", this.rascalDelay);
		tag.putInt("RascalChance", this.rascalChance);
		return tag;
	}
}
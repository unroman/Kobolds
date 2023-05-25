package net.salju.kobolds.worldgen;

import net.salju.kobolds.init.KoboldsStructures;

import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;

import java.util.function.Function;
import java.util.Optional;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Codec;

public class AbstractKoboldStructure extends Structure {
	public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
	public static final Codec<AbstractKoboldStructure> CODEC = RecordCodecBuilder.<AbstractKoboldStructure>mapCodec((codex) -> {
		return codex.group(settingsCodec(codex), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((temppool) -> {
			return temppool.startPool;
		}), ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((name) -> {
			return name.startJigsawName;
		}), Codec.intRange(0, 7).fieldOf("size").forGetter((intys) -> {
			return intys.maxDepth;
		}), HeightProvider.CODEC.fieldOf("start_height").forGetter((heighty) -> {
			return heighty.startHeight;
		}), Codec.BOOL.fieldOf("use_expansion_hack").forGetter((hacker) -> {
			return hacker.useExpansionHack;
		}), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((mapster) -> {
			return mapster.projectStartToHeightmap;
		}), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((maxster) -> {
			return maxster.maxDistanceFromCenter;
		})).apply(codex, AbstractKoboldStructure::new);
	}).flatXmap(verifyRange(), verifyRange()).codec();
	private final Holder<StructureTemplatePool> startPool;
	private final Optional<ResourceLocation> startJigsawName;
	private final int maxDepth;
	private final HeightProvider startHeight;
	private final boolean useExpansionHack;
	private final Optional<Heightmap.Types> projectStartToHeightmap;
	private final int maxDistanceFromCenter;

	private static Function<AbstractKoboldStructure, DataResult<AbstractKoboldStructure>> verifyRange() {
		return (verify) -> {
			byte b0;
			switch (verify.terrainAdaptation()) {
				case NONE :
					b0 = 0;
					break;
				case BURY :
				case BEARD_THIN :
				case BEARD_BOX :
					b0 = 12;
					break;
				default :
					throw new IncompatibleClassChangeError();
			}
			int i = b0;
			return verify.maxDistanceFromCenter + i > 128 ? DataResult.error(() -> {
				return "Structure size including terrain adaptation must not exceed 128";
			}) : DataResult.success(verify);
		};
	}

	public AbstractKoboldStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> pool, Optional<ResourceLocation> name, int inty, HeightProvider height, boolean hack, Optional<Heightmap.Types> map, int center) {
		super(config);
		this.startPool = pool;
		this.startJigsawName = name;
		this.maxDepth = inty;
		this.startHeight = height;
		this.useExpansionHack = hack;
		this.projectStartToHeightmap = map;
		this.maxDistanceFromCenter = center;
	}

	public AbstractKoboldStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> pool, int inty, HeightProvider height, boolean hack, Heightmap.Types map) {
		this(config, pool, Optional.empty(), inty, height, hack, Optional.of(map), 80);
	}

	public AbstractKoboldStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> pool, int inty, HeightProvider height, boolean hack) {
		this(config, pool, Optional.empty(), inty, height, hack, Optional.empty(), 80);
	}

	@Override
	public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
		ChunkPos chunk = context.chunkPos();
		int inty = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
		BlockPos pos = new BlockPos(chunk.getMinBlockX(), inty, chunk.getMinBlockZ());
		if (!biomeCheck(context, pos)) {
			return Optional.empty();
		}
		return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, pos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter);
	}

	@Override
	public StructureType<?> type() {
		return KoboldsStructures.KOBOLD_STRUCTURE.get();
	}

	protected boolean biomeCheck(GenerationContext context, BlockPos pos) {
		ChunkPos chunk = context.chunkPos();
		if (!(context.biomeSource() instanceof CheckerboardColumnBiomeSource)) {
			int range = 3;
			int y = pos.getY();
			if (projectStartToHeightmap.isPresent()) {
				y += context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), projectStartToHeightmap.get(), context.heightAccessor(), context.randomState());
			}
			y = QuartPos.fromBlock(y);
			for (int x = chunk.x - range; x <= chunk.x + range; x++) {
				for (int z = chunk.z - range; z <= chunk.z + range; z++) {
					Holder<Biome> biome = context.biomeSource().getNoiseBiome(QuartPos.fromSection(x), y, QuartPos.fromSection(z), context.randomState().sampler());
					if (biome.is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_ocean"))) || biome.is(TagKey.create(Registries.BIOME, new ResourceLocation("minecraft:is_river")))) {
						return false;
					}
				}
			}
		}
		BlockPos center = chunk.getMiddleBlockPosition(0);
		int height = context.chunkGenerator().getFirstOccupiedHeight(center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
		NoiseColumn noise = context.chunkGenerator().getBaseColumn(center.getX(), center.getZ(), context.heightAccessor(), context.randomState());
		BlockState state = noise.getBlock(center.getY() + height);
		if (!state.getFluidState().isEmpty()) {
			return false;
		}
		return true;
	}
}
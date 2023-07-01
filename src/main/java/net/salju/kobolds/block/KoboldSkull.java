package net.salju.kobolds.block;

import net.salju.kobolds.init.KoboldsModEntities;
import net.salju.kobolds.entity.KoboldSkeletonEntity;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class KoboldSkull extends Block implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public KoboldSkull() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).sound(SoundType.BONE_BLOCK).strength(0.7f, 4f).noOcclusion().randomTicks().isRedstoneConductor((bs, br, bp) -> false).dynamicShape().offsetType(Block.OffsetType.XZ));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Vec3 offset = state.getOffset(world, pos);
		return (switch (state.getValue(FACING)) {
			default -> Shapes.or(box(4.5, 0, 3.5, 11.5, 7, 10.5), box(5.5, 0, 10.5, 10.5, 3, 13.5));
			case NORTH -> Shapes.or(box(4.5, 0, 5.5, 11.5, 7, 12.5), box(5.5, 0, 2.5, 10.5, 3, 5.5));
			case EAST -> Shapes.or(box(3.5, 0, 4.5, 10.5, 7, 11.5), box(10.5, 0, 5.5, 13.5, 3, 10.5));
			case WEST -> Shapes.or(box(5.5, 0, 4.5, 12.5, 7, 11.5), box(2.5, 0, 5.5, 5.5, 3, 10.5));
		}).move(offset.x, offset.y, offset.z);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			if (!world.isClientSide() && !(world.isDay()) && world.canSeeSkyFromBelowWater(pos)) {
				world.destroyBlock(pos, false);
				if (world instanceof ServerLevel sev) {
					LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sev);
					bolt.moveTo(Vec3.atBottomCenterOf(pos));
					bolt.setVisualOnly(true);
					Mob skele = new KoboldSkeletonEntity(KoboldsModEntities.KOBOLD_SKELETON.get(), sev);
					skele.moveTo((x + 0.5), y, (z + 0.5), 0, 0);
					skele.finalizeSpawn(sev, world.getCurrentDifficultyAt(skele.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
					sev.addFreshEntity(bolt);
					world.addFreshEntity(skele);
				}
			}
		}
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (!world.isClientSide() && !(world.isDay()) && world.canSeeSkyFromBelowWater(pos)) {
			world.destroyBlock(pos, false);
			LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
			bolt.moveTo(Vec3.atBottomCenterOf(pos));
			bolt.setVisualOnly(true);
			Mob skele = new KoboldSkeletonEntity(KoboldsModEntities.KOBOLD_SKELETON.get(), world);
			skele.moveTo((x + 0.5), y, (z + 0.5), 0, 0);
			skele.finalizeSpawn(world, world.getCurrentDifficultyAt(skele.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
			world.addFreshEntity(bolt);
			world.addFreshEntity(skele);
		}
	}
}

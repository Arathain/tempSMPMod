package arathain.mason.item;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MerchantSimulacrumBlock extends Block implements Waterloggable {
    public MerchantSimulacrumBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(Properties.FACING, Direction.NORTH)).with(Properties.WATERLOGGED, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(6.0, 0.0, 6.0, 10.0, 14.0, 10.0);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.contains(Properties.WATERLOGGED) && (Boolean)state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            if (!world.isClient()) {
                world.setBlockState(pos, (BlockState)state.with(Properties.WATERLOGGED, true), 3);
                world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }

            return true;
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && (Boolean)state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = (BlockState)this.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing());
        if (!state.contains(Properties.WATERLOGGED)) {
            return state;
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            boolean source = fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
            return (BlockState)state.with(Properties.WATERLOGGED, source);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(new Property[]{Properties.FACING, Properties.WATERLOGGED}));
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.syncGlobalEvent(1023, pos, 0);
        world.getOtherEntities(player, (new Box(pos)).expand(100.0), (entity) -> {
            return entity instanceof SoulmouldEntity;
        }).forEach((soulmould) -> {
            ((SoulmouldEntity)soulmould).setActionState(2);
        });
    }
}

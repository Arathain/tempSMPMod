package arathain.mason.item;

import arathain.mason.entity.SoulmouldEntity;
import arathain.mason.init.MasonObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

public class StrongholdSoulmouldBlock extends Block {
    public StrongholdSoulmouldBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        SoulmouldEntity mould = new SoulmouldEntity(MasonObjects.SOULMOULD, world);
        mould.refreshPositionAndAngles(pos, 0, 0);
        mould.setDormantDir(state.get(Properties.HORIZONTAL_FACING).getOpposite());
        mould.setDormantPos(pos);
        mould.setActionState(2);
        world.spawnEntity(mould);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        SoulmouldEntity mould = new SoulmouldEntity(MasonObjects.SOULMOULD, world);
        mould.refreshPositionAndAngles(pos, 0, 0);
        mould.setDormantDir(state.get(Properties.HORIZONTAL_FACING).getOpposite());
        mould.setDormantPos(pos);
        mould.setActionState(2);
        world.spawnEntity(mould);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        super.onBlockAdded(state, world, pos, oldState, notify);
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        SoulmouldEntity mould = new SoulmouldEntity(MasonObjects.SOULMOULD, world);
        mould.refreshPositionAndAngles(pos, 0, 0);
        mould.setDormantDir(state.get(Properties.HORIZONTAL_FACING).getOpposite());
        mould.setDormantPos(pos);
        mould.setActionState(2);
        world.spawnEntity(mould);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        super.scheduledTick(state, world, pos, random);
    }
}

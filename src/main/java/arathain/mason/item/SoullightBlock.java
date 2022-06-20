package arathain.mason.item;

import arathain.mason.entity.RippedSoulEntity;
import arathain.mason.init.MasonObjects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.WorldAccess;

import java.util.Random;
import java.util.UUID;

public class SoullightBlock extends Block {
    public SoullightBlock(Settings settings) {
        super(settings);
    }

//    @Override
//    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
//        super.onBroken(world, pos, state);
//    }

//    @Override
//    public boolean hasRandomTicks(BlockState state) {
//        return true;
//    }
//
//    @Override
//    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
//        if(pos.isWithinDistance(new Vec3i(-300, pos.getY(), -260), 80)) {
//            RippedSoulEntity soul = new RippedSoulEntity(MasonObjects.RIPPED_SOUL, world);
//            soul.setOwnerUuid(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2"));
//            soul.setPos(pos.getX(), pos.getY(), pos.getZ());
//            world.spawnEntity(soul);
//        }
//    }
}

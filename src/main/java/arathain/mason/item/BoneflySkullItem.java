package arathain.mason.item;

import arathain.mason.entity.BoneflyEntity;
import arathain.mason.entity.SoulmouldEntity;
import arathain.mason.init.MasonObjects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BoneflySkullItem extends Item {
    public BoneflySkullItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        BlockPos pos = ctx.getBlockPos();
        Direction dir = ctx.getPlayerFacing();
        World world = ctx.getWorld();
        if(world.getBlockState(pos).getBlock().equals(Blocks.BONE_BLOCK) && isValid(world, pos, dir) && player.getOffHandStack().getItem().equals(Items.PHANTOM_MEMBRANE) && player.getOffHandStack().getCount() > 15) {
            world.breakBlock(pos, false);
            for(int i = 0; i < 7; i++) world.breakBlock(pos.offset(dir, i), false);
            for(int i = 0; i < 4; i++) world.breakBlock(pos.offset(dir, i).offset(Direction.UP), false);
            BoneflyEntity bonefly = new BoneflyEntity(MasonObjects.BONEFLY, world);
            bonefly.refreshPositionAndAngles(pos.offset(ctx.getSide()), 0, 0);
            bonefly.setOwner(player);
            ctx.getWorld().spawnEntity(bonefly);
            player.getOffHandStack().decrement(16);
            ctx.getStack().decrement(1);
        }

        return super.useOnBlock(ctx);
    }
    private boolean isValid(World world, BlockPos pos, Direction dir) {
        return world.getBlockState(pos.offset(dir)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir).offset(Direction.UP)).getBlock().equals(Blocks.BONE_BLOCK) && world.getBlockState(pos.offset(dir, 2).offset(Direction.UP)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 3).offset(Direction.UP)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 2)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 3)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 4)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 5)).getBlock().equals(Blocks.SOUL_SOIL) && world.getBlockState(pos.offset(dir, 6)).getBlock().equals(Blocks.BONE_BLOCK);
    }
}

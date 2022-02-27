package arathain.mason.item;

import arathain.mason.entity.SoulmouldEntity;
import arathain.mason.init.MasonObjects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SoulmouldItem extends Item {
    public SoulmouldItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        BlockPos pos = ctx.getBlockPos();
        Direction dir = ctx.getPlayerFacing();
        if((ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).isAir() && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP)).isAir() && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP, 2)).isAir())
                ||
                (ctx.getWorld().getBlockState(pos.offset(ctx.getSide())).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP)).getBlock().equals(Blocks.WATER) && ctx.getWorld().getBlockState(pos.offset(ctx.getSide()).offset(Direction.UP, 2)).getBlock().equals(Blocks.WATER))
        ) {
            SoulmouldEntity mould = new SoulmouldEntity(MasonObjects.SOULMOULD, ctx.getWorld());
            mould.refreshPositionAndAngles(pos.offset(ctx.getSide()), 0, 0);
            mould.setDormantDir(ctx.getPlayerFacing().getOpposite());
            mould.setDormantPos(pos.offset(ctx.getSide()));
            assert player != null;
            mould.setOwner(player);
            ctx.getWorld().spawnEntity(mould);
            ctx.getStack().decrement(1);
        }
        return super.useOnBlock(ctx);
    }
}

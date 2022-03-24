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
        if(world.getBlockState(pos).getBlock().equals(Blocks.BONE_BLOCK) && player.getOffHandStack().getItem().equals(Items.PHANTOM_MEMBRANE) && player.getOffHandStack().getCount() > 15) {
            world.breakBlock(pos.offset(dir), false);
            BoneflyEntity bonefly = new BoneflyEntity(MasonObjects.BONEFLY, world);
            bonefly.refreshPositionAndAngles(pos.offset(ctx.getSide()), 0, 0);
            bonefly.setOwner(player);
            ctx.getWorld().spawnEntity(bonefly);
            player.getOffHandStack().decrement(16);
            ctx.getStack().decrement(1);
        }

        return super.useOnBlock(ctx);
    }
}

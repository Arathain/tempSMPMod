package arathain.mason.mixin;

import arathain.mason.init.MasonObjects;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @Shadow @Final private boolean emitsParticles;

    public CampfireBlockMixin() {
    }

    @Inject(
            method = {"onEntityCollision"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void cancelEffigy(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (player.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack()) && !this.emitsParticles) {
                ci.cancel();
            }
        }

    }
}

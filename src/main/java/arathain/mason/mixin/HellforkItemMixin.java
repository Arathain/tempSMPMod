package arathain.mason.mixin;

import arathain.mason.init.MasonObjects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import org.ladysnake.impaled.common.item.HellforkItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HellforkItem.class)
public class HellforkItemMixin {
    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
    private Block swapWithSoulLight1(BlockState instance) {
        Block block = instance.getBlock();
        if (block == MasonObjects.SOULLIGHT) return Blocks.SOUL_CAMPFIRE;
        return block;
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 7))
    private Block swapWithSoulLight2(BlockState instance) {
        Block block = instance.getBlock();
        if (block == MasonObjects.SOULLIGHT) return Blocks.SOUL_WALL_TORCH;
        return block;
    }

    @ModifyExpressionValue(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;", ordinal = 3))
    private BlockState swapWithSoulfork(BlockState original, ItemUsageContext ctx) {
        if (ctx.getWorld().getBlockState(ctx.getBlockPos()).getBlock() == MasonObjects.SOULLIGHT) {
            return MasonObjects.TORCHLIGHT.getDefaultState();
        } else return original;
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 8))
    private Block swapWithTorchLight1(BlockState instance) {
        Block block = instance.getBlock();
        if (block == MasonObjects.TORCHLIGHT) return Blocks.CAMPFIRE;
        return block;
    }

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 15))
    private Block swapWithTorchLight2(BlockState instance) {
        Block block = instance.getBlock();
        if (block == MasonObjects.TORCHLIGHT) return Blocks.WALL_TORCH;
        return block;
    }

    @ModifyExpressionValue(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;", ordinal = 7))
    private BlockState swapWithHellfork(BlockState original, ItemUsageContext ctx) {
        if (ctx.getWorld().getBlockState(ctx.getBlockPos()).getBlock() == MasonObjects.TORCHLIGHT) {
            return MasonObjects.SOULLIGHT.getDefaultState();
        } else return original;
    }
}

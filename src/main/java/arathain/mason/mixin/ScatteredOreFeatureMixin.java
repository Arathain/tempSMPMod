package arathain.mason.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.ScatteredOreFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ScatteredOreFeature.class)
public class ScatteredOreFeatureMixin {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void scuffed(FeatureContext<OreFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        OreFeatureConfig oreFeatureConfig = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        int i = random.nextInt(oreFeatureConfig.size * 2 + 10);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int j = 0; j < i; ++j) {
            this.setPos(mutable, random, blockPos, Math.min(j, 13));
            BlockState blockState = structureWorldAccess.getBlockState(mutable);

            for(OreFeatureConfig.Target target : oreFeatureConfig.targets) {
                if (OreFeature.shouldPlace(blockState, structureWorldAccess::getBlockState, random, oreFeatureConfig, target, mutable)) {
                    structureWorldAccess.setBlockState(mutable, target.state, 2);
                    break;
                }
            }
        }

        cir.setReturnValue(true);

    }
    private void setPos(BlockPos.Mutable mutable, Random random, BlockPos origin, int spread) {
        int i = this.getSpread(random, spread);
        int j = this.getSpread(random, spread);
        int k = this.getSpread(random, spread);
        mutable.set(origin, i, j, k);
    }
    private int getSpread(Random random, int spread) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float)spread);
    }
}

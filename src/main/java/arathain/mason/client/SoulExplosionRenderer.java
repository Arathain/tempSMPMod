package arathain.mason.client;

import arathain.mason.entity.SoulExplosionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import static net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer.BEAM_TEXTURE;

@Environment(EnvType.CLIENT)
public class SoulExplosionRenderer extends EntityRenderer<SoulExplosionEntity> {
    public SoulExplosionRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(SoulExplosionEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.translate(-0.5, 0, -0.5);
        long time = entity.getWorld().getTime();

         if(entity.age > 500) {
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, time, 0, 256, DyeColor.YELLOW.getColorComponents(), 0.25F, 0.35F);
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, time, 0, -256, DyeColor.YELLOW.getColorComponents(), 0.25F, 0.35F);
         }
         if(entity.age > 540) {
            for (int i = 1; i < (4); i++) {
                float a = i / 2f * 3.141592f + Math.floorMod(time, 40)  + tickDelta;
                double cos = Math.cos(a);
                double sin = Math.sin(a);
                matrices.translate(cos * 4, 0, sin * 4);
                BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, time, 0, 256, DyeColor.ORANGE.getColorComponents(), 0.45F, 0.85F);
                BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, time, 0, -256, DyeColor.ORANGE.getColorComponents(), 0.45F, 0.85F);
                matrices.translate(-cos * 4, 0, -sin * 4);
            }
         }
        matrices.pop();

    }

    @Override
    public Identifier getTexture(SoulExplosionEntity entity) {
        return null;
    }
}

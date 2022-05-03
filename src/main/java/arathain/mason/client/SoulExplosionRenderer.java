package arathain.mason.client;

import arathain.mason.entity.SoulExplosionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SoulExplosionRenderer extends EntityRenderer<SoulExplosionEntity> {
    public SoulExplosionRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    @Override
    public void render(SoulExplosionEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SoulExplosionEntity entity) {
        return null;
    }
}

package arathain.mason.client;

import arathain.mason.entity.ChainsEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChainsEntityRenderer extends GeoEntityRenderer<ChainsEntity> {
    public ChainsEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChainsEntityModel());
    }

    @Override
    public void render(ChainsEntity chainsEntity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider provider, int packedLightIn) {
        super.render(chainsEntity, entityYaw, partialTicks, stack, provider, 15728880);
    }

    @Override
    public RenderLayer getRenderType(ChainsEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTexture(animatable));
    }

    @Override
    public Identifier getTexture(ChainsEntity chainsEntity) {
        return this.getTexture(chainsEntity);
    }
}

package arathain.mason.client;

import arathain.mason.entity.BoneflyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BoneflyEntityRenderer extends GeoEntityRenderer<BoneflyEntity> {
    public BoneflyEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BoneflyEntityModel());
    }

    @Override
    public RenderLayer getRenderType(BoneflyEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation, true);
    }
}

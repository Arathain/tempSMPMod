package arathain.mason.client;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SoulmouldEntityRenderer extends GeoEntityRenderer<SoulmouldEntity> {
    public SoulmouldEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SoulmouldEntityModel());
        this.addLayer(new SoulmouldEyesLayer(this));
    }

    @Override
    public RenderLayer getRenderType(SoulmouldEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation, true);
    }
}

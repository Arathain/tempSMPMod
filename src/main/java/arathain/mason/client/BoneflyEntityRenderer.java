package arathain.mason.client;

import arathain.mason.entity.BoneflyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BoneflyEntityRenderer extends GeoEntityRenderer<BoneflyEntity> {
    public BoneflyEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BoneflyEntityModel());
    }

    @Override
    public RenderLayer getRenderType(BoneflyEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture, true);
    }
}


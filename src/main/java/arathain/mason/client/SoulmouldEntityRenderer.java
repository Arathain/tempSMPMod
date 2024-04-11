package arathain.mason.client;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SoulmouldEntityRenderer extends GeoEntityRenderer<SoulmouldEntity> {
    public SoulmouldEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SoulmouldEntityModel());
        this.addRenderLayer(new SoulmouldEyesLayer(this));
    }

    @Override
    public Identifier getTexture(SoulmouldEntity entity) {
        return new Identifier("mason", "textures/entity/mould/soulmould.png");
    }

    public RenderLayer getRenderType(SoulmouldEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture, true);
    }
}


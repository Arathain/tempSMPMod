package arathain.mason.client;

import arathain.mason.entity.ChainsEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChainsEntityRenderer extends GeoEntityRenderer<ChainsEntity> {
    public ChainsEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChainsEntityModel());
    }

    @Override
    public void render(ChainsEntity chainsEntity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider provider, int packedLightIn) {
        super.render(chainsEntity, entityYaw, partialTicks, stack, provider, 15728880);
    }

    @Override
    public RenderLayer getRenderType(ChainsEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(this.getTexture(animatable));
    }

    @Override
    public Identifier getTexture(ChainsEntity chainsEntity) {
        return super.getTexture(chainsEntity);
    }
}

package arathain.mason.client;

import arathain.mason.MasonDecor;
import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class SoulmouldEyesLayer extends GeoLayerRenderer<SoulmouldEntity> {
    public SoulmouldEyesLayer(IGeoRenderer<SoulmouldEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, SoulmouldEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Identifier location = new Identifier(MasonDecor.MODID, "textures/entity/mould/eyes.png");
        RenderLayer armor = RenderLayer.getEyes(location);
        this.getRenderer().render(this.getEntityModel().getModel(this.getEntityModel().getModelResource(entitylivingbaseIn)), entitylivingbaseIn, partialTicks, armor, matrixStackIn, bufferIn, bufferIn.getBuffer(armor), -packedLightIn, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, (MathHelper.clamp(120f - packedLightIn, 0, 120f) / 160f));
    }
}

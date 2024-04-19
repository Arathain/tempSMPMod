package arathain.mason.client;

import arathain.mason.MasonDecor;
import arathain.mason.entity.SoulmouldEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SoulmouldEyesLayer extends GeoRenderLayer<SoulmouldEntity> {
    public SoulmouldEyesLayer(GeoEntityRenderer<SoulmouldEntity> entityRendererIn) {
        super(entityRendererIn);
    }


    @Override
    public void render(MatrixStack matrixStackIn, SoulmouldEntity entitylivingbaseIn, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferIn, VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
        Identifier location = new Identifier(MasonDecor.MODID, "textures/entity/mould/eyes.png");
        RenderLayer armor = RenderLayer.getEyes(location);
        this.getRenderer().reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entitylivingbaseIn)), matrixStackIn, bufferIn, entitylivingbaseIn, renderType, bufferIn.getBuffer(armor), partialTicks, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, (MathHelper.clamp(120f - packedLightIn, 0, 120f) / 160f));
    }
}
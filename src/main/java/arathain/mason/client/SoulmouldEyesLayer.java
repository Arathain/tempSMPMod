package arathain.mason.client;

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
    public void render(MatrixStack poseStack, SoulmouldEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        Identifier location = new Identifier("mason", "textures/entity/mould/eyes.png");
        RenderLayer armor = RenderLayer.getEyes(location);
        this.getRenderer().applyRenderLayersForBone(poseStack, animatable, this.getGeoModel().getBone("head").get(), renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}
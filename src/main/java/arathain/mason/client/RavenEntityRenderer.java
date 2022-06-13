package arathain.mason.client;

import arathain.mason.entity.RavenEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RavenEntityRenderer extends GeoEntityRenderer<RavenEntity> {
    private ItemStack itemStack;
    private VertexConsumerProvider vertexConsumerProvider;
    public RavenEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RavenEntityModel());
    }

    @Override
    public void renderEarly(RavenEntity ravenEntity, MatrixStack stackIn, float ticks, VertexConsumerProvider vertexConsumerProvider, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.itemStack = ravenEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        this.vertexConsumerProvider = vertexConsumerProvider;

        super.renderEarly(ravenEntity, stackIn, ticks, vertexConsumerProvider, vertexBuilder, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }
    @Override
    public RenderLayer getRenderType(RavenEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation, true);
    }
    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("body")) {
            stack.push();
            stack.translate(bone.getPositionX() / -16, bone.getPositionY() / 16 + 0.12f, -0.35f);
            stack.scale(0.5f, 0.5f, 0.5f);
            stack.multiply(new Quaternion(bone.getRotationX(), bone.getRotationZ(), bone.getRotationY(), false));

            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, this.vertexConsumerProvider, 0);
            stack.pop();

            // restore the render buffer - GeckoLib expects this state otherwise you'll have weird texture issues
            bufferIn = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(whTexture));
        }

        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

}

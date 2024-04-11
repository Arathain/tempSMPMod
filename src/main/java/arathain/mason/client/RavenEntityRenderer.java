package arathain.mason.client;

import arathain.mason.entity.RavenEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class RavenEntityRenderer extends GeoEntityRenderer<RavenEntity> {
    private ItemStack itemStack;
    private VertexConsumerProvider vertexConsumerProvider;

    public RavenEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RavenEntityModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, RavenEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.itemStack = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
        this.vertexConsumerProvider = bufferSource;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderLayer getRenderType(RavenEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture, false);
    }

    @Override
    public void renderRecursively(MatrixStack stack, RavenEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (bone.getName().equals("body")) {
            stack.push();
            stack.translate((bone.getPosX() / -16.0F), (bone.getPosY() / 16.0F + 0.12F), -0.3499999940395355);
            stack.scale(0.5F, 0.5F, 0.5F);
            stack.multiply(initQuaternionButSimple(bone.getRotX(), bone.getRotZ(), bone.getRotY(), false));
            MinecraftClient.getInstance().getItemRenderer().renderItem(this.itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, packedLight, packedOverlay, stack, this.vertexConsumerProvider, MinecraftClient.getInstance().world, 0);
            stack.pop();
            buffer = this.vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTextureLocation(animatable)));
        }

        super.renderRecursively(stack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static Quaternionf initQuaternionButSimple(float x, float y, float z, boolean isInDegrees) {
        Quaternionf quat = new Quaternionf();

        if (isInDegrees) {
            x *= 0.017453292F;
            y *= 0.017453292F;
            z *= 0.017453292F;
        }

        float f = sin(0.5F * x);
        float g = cos(0.5F * x);
        float h = sin(0.5F * y);
        float i = cos(0.5F * y);
        float j = sin(0.5F * z);
        float k = cos(0.5F * z);
        quat.x = f * i * k + g * h * j;
        quat.y = g * h * k - f * i * j;
        quat.z = f * h * k + g * i * j;
        quat.w = g * i * k - f * h * j;

        return quat;
    }
}

package arathain.mason.client;

import arathain.mason.entity.RippedSoulEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import org.ladysnake.effective.Effective;
import org.quiltmc.loader.api.QuiltLoader;
import team.lodestar.lodestone.systems.rendering.particle.Easing;
import team.lodestar.lodestone.systems.rendering.particle.WorldParticleBuilder;
import team.lodestar.lodestone.systems.rendering.particle.data.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.particle.data.GenericParticleData;

public class RippedSoulEntityRenderer extends EntityRenderer<RippedSoulEntity> {
    public RippedSoulEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    public void render(RippedSoulEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        DefaultParticleType overhead = ParticleTypes.SOUL_FIRE_FLAME;
        if (QuiltLoader.isModLoaded("effective")) {
           WorldParticleBuilder.create(Effective.WISP)
                   .setColorData(ColorParticleData.create(1.0F, 1.0F, 1.0F, -0.1F, -0.01F, 0.0F).build())
                   .setMotion(0.0, 0.0, 0.0)
                   .setScaleData(GenericParticleData.create(0.16f, 0f)
                           .setEasing(Easing.CIRC_OUT)
                           .build())
                   .spawn(entity.getWorld(), entity.getX() + entity.getRandom().nextGaussian() / 15.0, entity.getY() + entity.getRandom().nextGaussian() / 15.0, entity.getZ() + entity.getRandom().nextGaussian() / 15.0);
        } else {
            entity.getWorld().addParticle(overhead, entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
        }

    }

    public Identifier getTexture(RippedSoulEntity entity) {
        return null;
    }
}


package arathain.mason;

import arathain.mason.client.AnimatedStatueRenderer;
import arathain.mason.client.RavenEntityRenderer;
import arathain.mason.client.RavenFeatherParticle;
import arathain.mason.client.SoulmouldEntityRenderer;
import arathain.mason.init.MasonObjects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MasonDecorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initParticles();
        EntityRendererRegistry.register(MasonObjects.SOULMOULD, SoulmouldEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RAVEN, RavenEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.STATUE, AnimatedStatueRenderer::new);
    }

    public static final DefaultParticleType RAVEN_FEATHER = add("raven_feather");
    public static final DefaultParticleType RAVEN_FEATHER_ALBINO = add("raven_feather_albino");
    public static final DefaultParticleType RAVEN_FEATHER_GREEN = add("raven_feather_green");
    public static void initParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(RAVEN_FEATHER, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_ALBINO, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_GREEN, RavenFeatherParticle.Factory::new);
    }

    private static DefaultParticleType add(String name) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier("tot", name), FabricParticleTypes.simple());
    }
}

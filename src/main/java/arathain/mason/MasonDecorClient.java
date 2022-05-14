package arathain.mason;

import arathain.mason.client.*;
import arathain.mason.init.MasonObjects;
import arathain.mason.util.UpdatePressingUpDownPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

public class MasonDecorClient implements ClientModInitializer {
    private static KeyBinding DESCEND;
    @Override
    public void onInitializeClient() {
        initParticles();
        EntityRendererRegistry.register(MasonObjects.SOULMOULD, SoulmouldEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.SOUL_EXPLOSION, SoulExplosionRenderer::new);
        EntityRendererRegistry.register(MasonObjects.BONEFLY, BoneflyEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RAVEN, RavenEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.STATUE, AnimatedStatueRenderer::new);
        EntityRendererRegistry.register(MasonObjects.CHAINS, ChainsEntityRenderer::new);
        DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mason.descend", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "category.mason.keybind"
        ));
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdatePressingUpDownPacket.send(MinecraftClient.getInstance().options.keyJump.isPressed(), DESCEND.isPressed());

            }
        });
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

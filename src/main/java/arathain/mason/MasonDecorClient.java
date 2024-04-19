package arathain.mason;

import arathain.mason.client.*;
import arathain.mason.init.MasonObjects;
import arathain.mason.item.GlaiveItemRenderer;
import arathain.mason.util.UpdatePressingUpDownPacket;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.ResourceReloaderKeys;



public class MasonDecorClient implements ClientModInitializer {
    private static KeyBind DESCEND;
    @Override
    public void onInitializeClient(ModContainer mod) {
        initParticles();
        BlockRenderLayerMap.put(RenderLayer.getCutout(), MasonObjects.MERCHANT_SIMULACRUM);
        EntityRendererRegistry.register(MasonObjects.SOULMOULD, SoulmouldEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.SOUL_EXPLOSION, SoulExplosionRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RIPPED_SOUL, RippedSoulEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.BONEFLY, BoneflyEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RAVEN, RavenEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.CHAINS, ChainsEntityRenderer::new);
        DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBind(
                "key.mason.descend", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "category.mason.keybind"
        ));
        ClientTickEvents.END.register(world -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdatePressingUpDownPacket.send(MinecraftClient.getInstance().options.jumpKey.isPressed(), DESCEND.isPressed());

            }
        });

        Identifier scytheId = Registries.ITEM.getId(MasonObjects.GLAIVE);
        GlaiveItemRenderer scytheItemRenderer = new GlaiveItemRenderer(scytheId);
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(scytheItemRenderer);
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).addReloaderOrdering(ResourceReloaderKeys.Client.MODELS, scytheItemRenderer.getQuiltId());
        BuiltinItemRendererRegistry.INSTANCE.register(MasonObjects.GLAIVE, scytheItemRenderer);
        ModelLoadingPlugin.register((manager) -> {
            manager.addModels(
                    new ModelIdentifier(scytheId.getNamespace(), scytheId.getPath() + "_gui", "inventory"),
                    new ModelIdentifier(scytheId.getNamespace(), scytheId.getPath() + "_handheld", "inventory")
            );
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
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(MasonDecor.MODID, name), FabricParticleTypes.simple());
    }
}

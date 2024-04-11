package arathain.mason;

import arathain.mason.client.BoneflyEntityRenderer;
import arathain.mason.client.RavenEntityRenderer;
import arathain.mason.client.RavenFeatherParticle;
import arathain.mason.client.RippedSoulEntityRenderer;
import arathain.mason.client.SoulmouldEntityRenderer;
import arathain.mason.init.MasonObjects;
import arathain.mason.item.GlaiveItemRenderer;
import arathain.mason.util.UpdatePressingUpDownPacket;
import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.client.option.KeyBind;


public class MasonDecorClient implements ClientModInitializer {
    private static KeyBind DESCEND;
    public static final DefaultParticleType RAVEN_FEATHER = add("raven_feather");
    public static final DefaultParticleType RAVEN_FEATHER_ALBINO = add("raven_feather_albino");
    public static final DefaultParticleType RAVEN_FEATHER_GREEN = add("raven_feather_green");

    public MasonDecorClient() {
    }

    public void onInitializeClient() {
        initParticles();
        EntityRendererRegistry.register(MasonObjects.SOULMOULD, SoulmouldEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RIPPED_SOUL, RippedSoulEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.BONEFLY, BoneflyEntityRenderer::new);
        EntityRendererRegistry.register(MasonObjects.RAVEN, RavenEntityRenderer::new);
        DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBind("key.mason.descend", InputUtil.Type.KEYSYM, 71, "category.mason.keybind"));
        ClientTickEvents.END_CLIENT_TICK.register((world) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdatePressingUpDownPacket.send(MinecraftClient.getInstance().options.jumpKey.isPressed(), DESCEND.isPressed());
            }

        });

        Identifier weaponId = Registries.ITEM.getId(MasonObjects.GLAIVE);
        GlaiveItemRenderer scytheItemRenderer = new GlaiveItemRenderer(weaponId);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(scytheItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(MasonObjects.GLAIVE, scytheItemRenderer);
        ModelLoadingPlugin.register(new GlaiveItemModelProvider(weaponId));
    }

    public static void initParticles() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(RAVEN_FEATHER, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_ALBINO, RavenFeatherParticle.Factory::new);
        registry.register(RAVEN_FEATHER_GREEN, RavenFeatherParticle.Factory::new);
    }

    private static DefaultParticleType add(String name) {
        return (DefaultParticleType) Registry.register(Registries.PARTICLE_TYPE, new Identifier("mason", name), FabricParticleTypes.simple());
    }

    public static class GlaiveItemModelProvider implements ModelLoadingPlugin {
        Identifier weaponId;
        public GlaiveItemModelProvider(Identifier weaponId) {
            this.weaponId = weaponId;
        }
        @Override
        public void onInitializeModelLoader(Context pluginContext) {
            pluginContext.addModels(
                    new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_gui", "inventory"),
                    new ModelIdentifier(weaponId.getNamespace(), weaponId.getPath() + "_handheld", "inventory")
            );
        }
    }
}
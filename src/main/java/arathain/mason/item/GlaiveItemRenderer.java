package arathain.mason.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.ResourceReloaderKeys;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import java.util.Collection;
import java.util.Collections;

public class GlaiveItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloader {
    private final Identifier id;
    private final Identifier scytheId;
    private ItemRenderer itemRenderer;
    private BakedModel inventoryScytheModel;
    private BakedModel worldScytheModel;

    public GlaiveItemRenderer(Identifier tridentId) {
        this.id = new Identifier(tridentId.getNamespace(), tridentId.getPath() + "_renderer");
        this.scytheId = tridentId;
    }

    @Override
    public @NotNull Identifier getQuiltId() {
        return this.id;
    }


    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient mc = MinecraftClient.getInstance();
        this.itemRenderer = mc.getItemRenderer();
        this.inventoryScytheModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.scytheId + "_gui", "inventory"));
        this.worldScytheModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.scytheId + "_handheld", "inventory"));
    }
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (mode != ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND && mode != ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND && mode != ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND && mode != ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) {
            matrices.pop();
            matrices.push();
            itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryScytheModel);
        } else {
            matrices.pop();
            matrices.push();
            boolean leftHanded;
            switch(mode) {
                case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
                default -> leftHanded = false;
            }
            itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldScytheModel);
        }
    }
}

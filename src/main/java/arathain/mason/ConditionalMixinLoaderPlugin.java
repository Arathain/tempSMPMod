package arathain.mason;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ConditionalMixinLoaderPlugin implements IMixinConfigPlugin {

    private static final Supplier<Boolean> TRUE = () -> true;

    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.<String, Supplier<Boolean>>builder()
            .put(mixinPackage("HellforkItemMixin"), () -> isModPresent("impaled"))
            .build();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private static String mixinPackage(String a) {
        return "arathain.mason.mixin." + a;
    }

    private static boolean isModPresent(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
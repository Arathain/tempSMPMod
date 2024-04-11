package arathain.mason;

import arathain.mason.init.MasonObjects;
import arathain.mason.util.GlaivePacket;
import arathain.mason.util.UpdatePressingUpDownPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.tag.BiomeTags;
import software.bernie.geckolib.GeckoLib;

public class MasonDecor implements ModInitializer {
    public static final String MODID = "mason";

    public MasonDecor() {
    }

    public void onInitialize() {
        //GeckoLibMod.DISABLE_IN_DEV = true;
        GeckoLib.initialize();
        MasonObjects.init();
        ServerPlayNetworking.registerGlobalReceiver(GlaivePacket.ID, GlaivePacket::handle);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.FOREST), SpawnGroup.CREATURE, MasonObjects.RAVEN, 8, 2, 5);
        ServerPlayNetworking.registerGlobalReceiver(UpdatePressingUpDownPacket.ID, UpdatePressingUpDownPacket::handle);
    }
}

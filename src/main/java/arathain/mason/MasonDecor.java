package arathain.mason;

import arathain.mason.init.MasonObjects;
import arathain.mason.util.UpdatePressingUpDownPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.gen.feature.StrongholdFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasonDecor implements ModInitializer {
	public static final String MODID = "mason";

	@Override
	public void onInitialize() {
		MasonObjects.init();
		BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.FOREST), SpawnGroup.CREATURE, MasonObjects.RAVEN, 8, 2, 5);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePressingUpDownPacket.ID, UpdatePressingUpDownPacket::handle);

	}
}

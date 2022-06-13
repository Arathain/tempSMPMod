package arathain.mason;

import arathain.mason.init.MasonObjects;
import arathain.mason.util.GlaivePacket;
import arathain.mason.util.UpdatePressingUpDownPacket;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.BiomeTags;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

public class MasonDecor implements ModInitializer {
	public static final String MODID = "mason";

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLibMod.DISABLE_IN_DEV = true;
		GeckoLib.initialize();
		MasonObjects.init();
		ServerPlayNetworking.registerGlobalReceiver(GlaivePacket.ID, GlaivePacket::handle);
		BiomeModifications.addSpawn(BiomeSelectors.isIn(BiomeTags.IS_FOREST), SpawnGroup.CREATURE, MasonObjects.RAVEN, 8, 2, 5);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePressingUpDownPacket.ID, UpdatePressingUpDownPacket::handle);
	}
}

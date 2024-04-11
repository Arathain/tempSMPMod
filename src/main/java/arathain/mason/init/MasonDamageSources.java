package arathain.mason.init;

import arathain.mason.MasonDecor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MasonDamageSources {

    public static RegistryKey<DamageType> SOUL_RIP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MasonDecor.MODID, "soul_rip"));

    static DamageSource playerRip(World world, Entity attacker) {
        return create(world, SOUL_RIP);
    }

    static DamageSource create(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getHolderOrThrow(key));
    }
}

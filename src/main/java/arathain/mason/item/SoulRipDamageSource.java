package arathain.mason.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SoulRipDamageSource extends DamageSource {
    protected final Entity source;
    public static DamageSource playerRip(PlayerEntity attacker) {
        return new SoulRipDamageSource("soul_rip", attacker).setUsesMagic();
    }

    public SoulRipDamageSource(String name, PlayerEntity source) {
        super(name);
        this.source = source;
    }
    @Override
    public Entity getAttacker() {
        return this.source;
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d getPosition() {
        return this.source.getPos();
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.source + ")";
    }
}
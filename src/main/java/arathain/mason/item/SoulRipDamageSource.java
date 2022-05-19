package arathain.mason.item;

import net.minecraft.entity.damage.DamageSource;

public class SoulRipDamageSource extends DamageSource {
    public static final DamageSource SOUL_RIP = new SoulRipDamageSource("soul_rip").setBypassesArmor().setUsesMagic();

    protected SoulRipDamageSource(String name) {
        super(name);
    }
}
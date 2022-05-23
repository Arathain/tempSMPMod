package arathain.mason.mixin;

import arathain.mason.entity.RippedSoulEntity;
import arathain.mason.init.MasonObjects;
import arathain.mason.item.SoulRipDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity  {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void malum$onDeath(DamageSource source, CallbackInfo ci) {
        if(!world.isClient && source instanceof SoulRipDamageSource ripSource) {
            RippedSoulEntity soul = new RippedSoulEntity(MasonObjects.RIPPED_SOUL, this.getWorld());
            assert ripSource.getAttacker() != null;
            soul.setOwner((PlayerEntity) ripSource.getAttacker());
            soul.setPosition(this.getPos().add(0, 1, 0));
            this.world.spawnEntity(soul);
        }
    }
}

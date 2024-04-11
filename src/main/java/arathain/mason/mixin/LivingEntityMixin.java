package arathain.mason.mixin;

import arathain.mason.entity.RippedSoulEntity;
import arathain.mason.init.MasonDamageSources;
import arathain.mason.init.MasonObjects;
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

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = {"onDeath"},
            at = {@At("HEAD")}
    )
    private void mason$onDeath(DamageSource source, CallbackInfo ci) {
        if (!this.getWorld().isClient && source.isType(MasonDamageSources.SOUL_RIP)) {
            RippedSoulEntity soul = new RippedSoulEntity(MasonObjects.RIPPED_SOUL, this.getWorld());

            assert source.getAttacker() != null;

            soul.setOwner((PlayerEntity)source.getAttacker());
            soul.setPosition(this.getPos().add(0.0, 1.0, 0.0));
            this.getWorld().spawnEntity(soul);
        }

    }
}


package arathain.mason.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChainsEntity extends HostileEntity implements GeoAnimatable {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    public ChainsEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.inanimate = true;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.isType(DamageTypes.OUT_OF_WORLD);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        passenger.setPosition(this.getX(), this.getY() - 0.3, this.getZ());
        passenger.setBodyYaw(MathHelper.clamp(passenger.getYaw(), this.getYaw() - 10f, this.getYaw() + 10f));
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.hasPassengers())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.getWorld().isClient || this.isRemoved()) {
            return true;
        }
        this.scheduleVelocityUpdate();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        if ((!(source.getAttacker() == this.getFirstPassenger()) && amount >= 6) || source.isTypeIn(DamageTypeTags.IS_EXPLOSION) || source.isTypeIn(DamageTypeTags.IS_FIRE) || source.isSourceCreativePlayer()) {
            this.discard();
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, animationEvent -> {
            RawAnimation animationBuilder = RawAnimation.begin();
            animationBuilder.thenPlay("apparate");
            animationBuilder.thenLoop("idle");
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}

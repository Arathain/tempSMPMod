package arathain.mason.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ChainsEntity extends HostileEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
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
        return damageSource.isOutOfWorld();
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }
    @Override
    public void updatePassengerPosition(Entity passenger) {
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
        if (this.world.isClient || this.isRemoved()) {
            return true;
        }
        this.scheduleVelocityUpdate();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        if ((!(source.getAttacker() == this.getFirstPassenger()) && amount >= 6) || source.isExplosive() || source.isFire() || source.isSourceCreativePlayer()) {
            this.discard();
        }
        return true;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 2, animationEvent -> {
            animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("apparate", false).addAnimation("idle", true));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

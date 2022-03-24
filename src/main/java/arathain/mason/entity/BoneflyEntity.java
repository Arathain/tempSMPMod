package arathain.mason.entity;

import arathain.mason.entity.goal.SoulmouldAttackLogicGoal;
import arathain.mason.entity.goal.SoulmouldDashSlashGoal;
import arathain.mason.entity.goal.TamedAttackWithOwnerGoal;
import arathain.mason.entity.goal.TamedTrackAttackerGoal;
import arathain.mason.init.MasonComponents;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BoneflyEntity extends HostileEntity implements IAnimatable, TameableHostileEntity {
    private final AnimationFactory factory = new AnimationFactory(this);
    protected static final TrackedData<Boolean> DORMANT = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(BoneflyEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public BoneflyEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer.Builder createBoneflyAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 60).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f).add(EntityAttributes.GENERIC_ARMOR, 24f);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DORMANT, false);
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
    }
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
    @Override
    protected void initGoals() {}

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {

    }

    @Override
    public boolean isClimbing() {
        return false;
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("Dormant", this.isDormant());
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID ownerUUID;
        if (nbt.containsUuid("Owner")) {
            ownerUUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            ownerUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }

        if (ownerUUID != null) {
            try {
                this.setOwnerUuid(ownerUUID);
                this.setTamed(true);
            } catch (Throwable var4) {
                this.setTamed(false);
            }
        }
        this.setDormant(nbt.getBoolean("Dormant"));
    }
    public boolean isDormant() {
        return getDataTracker().get(DORMANT);
    }

    public void setDormant(boolean rest) {
        getDataTracker().set(DORMANT, rest);
    }
    @Override
    public void travel(Vec3d travelVector) {
        boolean flying = !this.isOnGround();
        float speed = (float) this.getAttributeValue(flying ? EntityAttributes.GENERIC_FLYING_SPEED : EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (!this.hasPassengers() && !this.canBeControlledByRider()) {
            this.airStrafingSpeed = 0.02f;
            super.travel(travelVector);
            return;
        }
        LivingEntity passenger = (LivingEntity) this.getFirstPassenger();
        if (passenger != null) {
            this.headYaw = (float) this.serverYaw;
            this.serverHeadYaw = this.headYaw;
            this.serverYaw = this.serverYaw - passenger.sidewaysSpeed * 2f;
            this.serverPitch = passenger.getPitch() * 0.5F;
            boolean isPlayerUpwardsMoving = MasonComponents.RIDER_COMPONENT.get(passenger).isPressingUp();
            boolean isPlayerDownwardsMoving = MasonComponents.RIDER_COMPONENT.get(passenger).isPressingDown();
            double getFlightDelta = isPlayerUpwardsMoving ? 0.8 : isPlayerDownwardsMoving ? -0.6 : 0;
            this.setPitch((float) this.serverPitch);
            this.setYaw((float) this.serverYaw);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = (float) this.serverYaw;

            if (!flying && isPlayerUpwardsMoving) this.jump();

            if (this.getFirstPassenger() != null) {
                travelVector = new Vec3d(0, getFlightDelta, passenger.forwardSpeed * 0.5);
                this.setMovementSpeed(speed);
                this.stepBobbingAmount = 0;
            } else if (passenger instanceof PlayerEntity) {
                this.updateLimbs(this, false);
                this.setVelocity(Vec3d.ZERO);
                return;
            }
        }
        if (flying) {
            this.applyMovementInput(travelVector, speed);
            this.move(MovementType.SELF, getVelocity());
            this.setVelocity(getVelocity().multiply(0.91f));
            this.updateLimbs(this, false);
            this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }
        else {
            super.travel(travelVector);
            this.updateLeash();
        }
    }
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (this.isOwner(player) && !isBaby() && stack.isEmpty() && this.isTamed() && !this.hasPassengers()) {
            player.startRiding(this);
            this.navigation.stop();
        }

        return super.interactMob(player, hand);
    }

    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
    }
    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float f = 0.5f;
        float g = (float)((this.isRemoved() ? (double)0.01f : this.getMountedHeightOffset()) + passenger.getHeightOffset());
        Vec3d vec3d = new Vec3d(f, 0.0, 0.0).rotateY(-this.getYaw() * ((float)Math.PI / 180) - 1.5707964f);
        passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
        passenger.setYaw(passenger.getYaw());
        passenger.setHeadYaw(passenger.getHeadYaw());
    }

    @Override
    public double getMountedHeightOffset() {
        return 2.3;
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getFirstPassenger() instanceof LivingEntity;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "wingController", 5, this::wingPredicate));
        animationData.addAnimationController(new AnimationController<>(this, "idleController", 1, this::idlePredicate));
    }
    private <E extends IAnimatable> PlayState wingPredicate(AnimationEvent<E> event) {
        AnimationBuilder animationBuilder = new AnimationBuilder();
        if (!this.isOnGround()) {
            animationBuilder.addAnimation("fly", true);
        } else {
            //animationBuilder.addAnimation("stabIdle", true);
        }

        if(!animationBuilder.getRawAnimationList().isEmpty()) {
            event.getController().setAnimation(animationBuilder);
        }
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        AnimationBuilder animationBuilder = new AnimationBuilder();
        if(this.hurtTime > 0 || this.deathTime > 0) {
            animationBuilder.addAnimation("hurt", true);
        } else if(this.isDormant()) {
            animationBuilder.addAnimation("idleDormant", true);
        } else {
            animationBuilder.addAnimation("idle", true);
        }

        if(!animationBuilder.getRawAnimationList().isEmpty()) {
            event.getController().setAnimation(animationBuilder);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public UUID getOwnerUuid() {
        return (UUID) ((Optional) this.dataTracker.get(OWNER_UUID)).orElse(null);
    }

    @Override
    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
    }
    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            return uUID == null ? null : this.world.getPlayerByUuid(uUID);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    public void setTamed(boolean tamed) {

    }
}

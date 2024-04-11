package arathain.mason.entity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RippedSoulEntity extends HostileEntity {
    protected static final TrackedData<Byte> VEX_FLAGS;
    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final int CHARGING_FLAG = 1;
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;

    public RippedSoulEntity(EntityType<RippedSoulEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new VexMoveControl(this);
        this.experiencePoints = 3;
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return effect.getEffectType() == StatusEffects.WITHER || effect.getEffectType() == StatusEffects.INSTANT_DAMAGE || effect.getEffectType() == StatusEffects.INSTANT_HEALTH;
    }

    public boolean damage(DamageSource source, float amount) {
        return (this.getWorld().isClient() || !source.isTypeIn(DamageTypeTags.IS_EXPLOSION)) && super.damage(source, amount);
    }

    public boolean hasWings() {
        return this.age % MathHelper.ceil(3.9269907F) == 0;
    }

    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }

    public static DefaultAttributeContainer.Builder createVexAttributes() {
        return HostileEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(8, new LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new TargetGoal(this, PlayerEntity.class, true, (player) -> {
            return !this.isOwner((LivingEntity) player);
        }));
    }

    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
        if (this.age > 1200 && this.random.nextInt(20) == 1 && !this.alive) {
            this.alive = true;
        }

        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(this.getWorld().getDamageSources().starve(), 1.0F);
        }

    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
    }

    public Text getName() {
        return this.getOwner().getName() == null ? super.getName() : this.getOwner().getName();
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }

        if (nbt.contains("LifeTicks")) {
            this.setLifeTicks(nbt.getInt("LifeTicks"));
        }

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
            } catch (Throwable var4) {
            }
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }

        if (this.alive) {
            nbt.putInt("LifeTicks", this.lifeTicks);
        }

    }

    public @Nullable BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable BlockPos bounds) {
        this.bounds = bounds;
    }

    private boolean areFlagsSet(int mask) {
        byte i = (Byte)this.dataTracker.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = (Byte)this.dataTracker.get(VEX_FLAGS);
        i = value ? i | mask : i & ~mask;
        this.dataTracker.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    public UUID getOwnerUuid() {
        return (UUID)((Optional)this.dataTracker.get(OWNER_UUID)).orElse((Object)null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity player) {
        if (player != null) this.setOwnerUuid(player.getUuid());
    }

    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            return uUID == null ? null : this.getWorld().getPlayerByUuid(uUID);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public void setLifeTicks(int lifeTicks) {
        this.alive = true;
        this.lifeTicks = lifeTicks;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    public float getLightLevelDependentValue() {
        return 1.0F;
    }

    static {
        VEX_FLAGS = DataTracker.registerData(RippedSoulEntity.class, TrackedDataHandlerRegistry.BYTE);
        OWNER_UUID = DataTracker.registerData(RippedSoulEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }

    class VexMoveControl extends MoveControl {
        public VexMoveControl(RippedSoulEntity owner) {
            super(owner);
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - RippedSoulEntity.this.getX(), this.targetY - RippedSoulEntity.this.getY(), this.targetZ - RippedSoulEntity.this.getZ());
                double d = vec3d.length();
                if (d < RippedSoulEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    RippedSoulEntity.this.setVelocity(RippedSoulEntity.this.getVelocity().multiply(0.5));
                } else {
                    RippedSoulEntity.this.setVelocity(RippedSoulEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                    if (RippedSoulEntity.this.getTarget() == null) {
                        Vec3d vec3d2 = RippedSoulEntity.this.getVelocity();
                        RippedSoulEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                        RippedSoulEntity.this.bodyYaw = RippedSoulEntity.this.getYaw();
                    } else {
                        double e = RippedSoulEntity.this.getTarget().getX() - RippedSoulEntity.this.getX();
                        double f = RippedSoulEntity.this.getTarget().getZ() - RippedSoulEntity.this.getZ();
                        RippedSoulEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                        RippedSoulEntity.this.bodyYaw = RippedSoulEntity.this.getYaw();
                    }
                }

            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            if (RippedSoulEntity.this.getTarget() != null && !RippedSoulEntity.this.getMoveControl().isMoving() && RippedSoulEntity.this.random.nextInt(toGoalTicks(7)) == 0) {
                return RippedSoulEntity.this.squaredDistanceTo(RippedSoulEntity.this.getTarget()) > 4.0;
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            return RippedSoulEntity.this.getMoveControl().isMoving() && RippedSoulEntity.this.isCharging() && RippedSoulEntity.this.getTarget() != null && RippedSoulEntity.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingEntity = RippedSoulEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                RippedSoulEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }

            RippedSoulEntity.this.setCharging(true);
            RippedSoulEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        public void stop() {
            RippedSoulEntity.this.setCharging(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = RippedSoulEntity.this.getTarget();
            if (livingEntity != null) {
                if (RippedSoulEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                    RippedSoulEntity.this.tryAttack(livingEntity);
                    RippedSoulEntity.this.setCharging(false);
                } else {
                    double d = RippedSoulEntity.this.squaredDistanceTo(livingEntity);
                    if (d < 9.0) {
                        Vec3d vec3d = livingEntity.getEyePos();
                        RippedSoulEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
                    }
                }

            }
        }
    }

    class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            return !RippedSoulEntity.this.getMoveControl().isMoving() && RippedSoulEntity.this.random.nextInt(toGoalTicks(7)) == 0;
        }

        public boolean shouldContinue() {
            return false;
        }

        public void tick() {
            BlockPos blockPos = RippedSoulEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = RippedSoulEntity.this.getBlockPos();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(RippedSoulEntity.this.random.nextInt(15) - 7, RippedSoulEntity.this.random.nextInt(11) - 5, RippedSoulEntity.this.random.nextInt(15) - 7);
                if (RippedSoulEntity.this.getWorld().isAir(blockPos2)) {
                    RippedSoulEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
                    if (RippedSoulEntity.this.getTarget() == null) {
                        RippedSoulEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class TrackOwnerTargetGoal extends TrackTargetGoal {
        private final TargetPredicate targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

        public TrackOwnerTargetGoal(PathAwareEntity mob) {
            super(mob, false);
        }

        public boolean canStart() {
            return RippedSoulEntity.this.getOwner() != null && RippedSoulEntity.this.getOwner().getAttacking() != null && this.canTrack(RippedSoulEntity.this.getOwner().getAttacking(), this.targetPredicate);
        }

        public void start() {
            RippedSoulEntity.this.setTarget(RippedSoulEntity.this.getOwner().getAttacking());
            super.start();
        }
    }
}
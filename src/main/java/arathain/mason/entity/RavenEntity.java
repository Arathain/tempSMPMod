package arathain.mason.entity;

import arathain.mason.MasonDecorClient;
import arathain.mason.entity.goal.RavenDeliverBundleGoal;
import arathain.mason.entity.goal.RavenFollowOwnerGoal;
import arathain.mason.init.MasonObjects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RavenEntity extends TameableEntity implements GeoAnimatable {
    private static final TrackedData<Optional<UUID>> RECEIVER_UUID;
    public static final TrackedData<String> TYPE;
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final TrackedData<Boolean> SITTING;
    public static final TrackedData<Boolean> GOING_TO_RECEIVER;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    private float whuhhuh = 1.0F;

    public RavenEntity(EntityType<? extends TameableEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 90, false);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, new RavenDeliverBundleGoal<>(this, 1.0, 6.0F, 128.0F, false));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new RavenFollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(5, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(0, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createRavenAttributes() {
        return MobEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.7);
    }

    protected void initEquipment(RandomGenerator randomGenerator, LocalDifficulty difficulty) {
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 1.0F);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("sitting", this.isSitting());
        nbt.putBoolean("goin", (Boolean)this.dataTracker.get(GOING_TO_RECEIVER));
        if (this.getReceiverUuid() != null) {
            nbt.putUuid("Receiver", this.getReceiverUuid());
        }

        nbt.putString("Type", this.getRavenType().toString());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSitting(nbt.getBoolean("sitting"));
        this.dataTracker.set(GOING_TO_RECEIVER, nbt.getBoolean("goin"));
        if (nbt.contains("Type")) {
            this.setRavenType(RavenEntity.Type.valueOf(nbt.getString("Type")));
        }

        if (nbt.containsUuid("Receiver")) {
            this.setReceiverUuid(nbt.getUuid("Receiver"));
        } else {
            String string = nbt.getString("Receiver");
            this.setReceiverUuid(ServerConfigHandler.getPlayerUuidByName(this.getServer(), string));
        }

    }

    public Type getRavenType() {
        return RavenEntity.Type.valueOf((String)this.dataTracker.get(TYPE));
    }

    public void setRavenType(Type type) {
        this.dataTracker.set(TYPE, type.toString());
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(RECEIVER_UUID, Optional.empty());
        this.dataTracker.startTracking(GOING_TO_RECEIVER, false);
        if (this.random.nextInt(11) == 0) {
            this.dataTracker.startTracking(TYPE, RavenEntity.Type.ALBINO.toString());
        } else {
            this.dataTracker.startTracking(TYPE, this.random.nextBoolean() ? RavenEntity.Type.DARK.toString() : RavenEntity.Type.SEA_GREEN.toString());
        }

    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().getFoodComponent() != null && stack.getItem().getFoodComponent().isMeat();
    }

    protected void mobTick() {
        super.mobTick();
        ItemStack stack = this.getStackInHand(Hand.MAIN_HAND);
        if (!stack.isEmpty() && stack.hasCustomName()) {
            PlayerEntity entity = this.getServer().getPlayerManager().getPlayer(stack.getName().toString());
            if (entity != null && entity.getUuid() != null && !stack.getName().toString().contains("Mouthpiece")) {
                this.setReceiverUuid(entity.getUuid());
            } else {
                this.setReceiverUuid((UUID)null);
                this.dataTracker.set(GOING_TO_RECEIVER, false);
            }
        } else {
            this.setReceiverUuid((UUID)null);
            this.dataTracker.set(GOING_TO_RECEIVER, false);
        }

        if (this.hasCustomName()) {
            String name = this.getCustomName().getString();
            if (name.equalsIgnoreCase("three_eyed") || name.equalsIgnoreCase("three_eyed_raven") || name.equalsIgnoreCase("three eyed") || name.equalsIgnoreCase("three eyed raven") || name.equalsIgnoreCase("three-eyed raven")) {
                this.setRavenType(RavenEntity.Type.THREE_EYED);
            }
        }

    }

    public void spawnFeatherParticles(int count) {
        if (this.getWorld().isClient) {
            float height = this.getHeight();
            if (height * 100.0F < 100.0F) {
                height = 1.0F;
            } else {
                height += 0.5F;
            }

            for(int i = 0; i <= count; ++i) {
                double randomHeight = (double)this.random.nextInt((int)height * 10) / 10.0;
                World var10000 = this.getWorld();
                DefaultParticleType var10001;
                switch (this.getRavenType()) {
                    case DARK:
                    case THREE_EYED:
                        var10001 = MasonDecorClient.RAVEN_FEATHER;
                        break;
                    case ALBINO:
                        var10001 = MasonDecorClient.RAVEN_FEATHER_ALBINO;
                        break;
                    case SEA_GREEN:
                        var10001 = MasonDecorClient.RAVEN_FEATHER_GREEN;
                        break;
                    default:
                        throw new IncompatibleClassChangeError();
                }

                var10000.addParticle(var10001, this.getX(), this.getY() + 0.2 + randomHeight, this.getZ(), 0.0, 0.0, 0.0);
            }
        }

    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem().equals(Items.BUNDLE) && stack.hasCustomName()) {
            if (!this.getWorld().isClient) {
                this.setStackInHand(Hand.MAIN_HAND, stack);
                player.setStackInHand(hand, ItemStack.EMPTY);
            }

            return ActionResult.success(this.getWorld().isClient);
        } else if (stack.isEmpty() && this.getStackInHand(Hand.MAIN_HAND).getItem().equals(Items.BUNDLE) && !player.isSneaking()) {
            if (!this.getWorld().isClient) {
                player.setStackInHand(hand, this.getStackInHand(Hand.MAIN_HAND));
                this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }

            return ActionResult.success(this.getWorld().isClient);
        } else if (this.isOnGround() && this.isTamed() && this.isOwner(player) && stack.isEmpty()) {
            if (!this.getWorld().isClient) {
                this.setSitting(!this.isSitting());
            }

            return ActionResult.success(this.getWorld().isClient);
        } else {
            if (!this.isTamed()) {
                if (this.isBreedingItem(stack)) {
                    if (!this.getWorld().isClient()) {
                        this.eat(player, hand, stack);
                        if (this.random.nextInt(4) == 0) {
                            this.setOwner(player);
                            this.setSitting(true);
                            this.setTarget((LivingEntity)null);
                            this.navigation.stop();
                            this.getWorld().sendEntityStatus(this, (byte)7);
                        } else {
                            this.getWorld().sendEntityStatus(this, (byte)6);
                        }
                    }

                    return ActionResult.success(this.getWorld().isClient());
                }
            } else if (this.isBreedingItem(stack) && this.getHealth() < this.getMaxHealth()) {
                if (!this.getWorld().isClient()) {
                    this.eat(player, hand, stack);
                    this.heal(4.0F);
                }

                return ActionResult.success(this.getWorld().isClient());
            }

            return super.interactMob(player, hand);
        }
    }

    public UUID getReceiverUuid() {
        return (UUID)((Optional)this.dataTracker.get(RECEIVER_UUID)).orElse((Object)null);
    }

    public void setReceiverUuid(@Nullable UUID uuid) {
        this.dataTracker.set(RECEIVER_UUID, Optional.ofNullable(uuid));
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
    }

    public boolean isPushable() {
        return true;
    }

    protected void addFlapEffects() {
        if (!this.isSitting()) {
            this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
        }

        this.whuhhuh = this.flyDistance + this.maxWingDeviation / 2.0F;
    }

    public void tickMovement() {
        super.tickMovement();
        this.flapWings();
    }

    private void flapWings() {
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation += (float)(!this.isOnGround() && !this.hasVehicle() ? 4 : -1) * 0.3F;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    protected boolean hasWings() {
        return this.flyDistance > this.whuhhuh;
    }

    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RavenEntity child = (RavenEntity)MasonObjects.RAVEN.create(world);
        if (child != null) {
            child.initialize(world, world.getLocalDifficulty(this.getBlockPos()), SpawnReason.BREEDING, (EntityData)null, (NbtCompound)null);
            UUID owner = this.getOwnerUuid();
            if (owner != null) {
                child.setOwnerUuid(owner);
                child.setTamed(true);
            }

            if (entity instanceof RavenEntity && this.random.nextFloat() < 0.95F) {
                child.dataTracker.set(TYPE, this.random.nextBoolean() ? (String)this.dataTracker.get(TYPE) : (String)entity.getDataTracker().get(TYPE));
            }
        }

        return child;
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        RawAnimation animationBuilder = RawAnimation.begin();
        if (!this.isOnGround()) {
            animationBuilder.thenLoop(Math.abs(this.getVelocity().y) > 0.10000000149011612 ? "fastFly" : "fly");
            event.getController().setAnimation(animationBuilder);
        } else if (!(Boolean)this.dataTracker.get(SITTING) && !this.hasVehicle()) {
            animationBuilder.thenLoop("idle");
            event.getController().setAnimation(animationBuilder);
        } else {
            animationBuilder.thenLoop("sitIdle");
            event.getController().setAnimation(animationBuilder);
        }

        return PlayState.CONTINUE;
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (target instanceof TameableEntity && ((TameableEntity)target).isTamed()) {
            return false;
        } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()) {
            return false;
        } else {
            if (target instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)target;
                if (owner instanceof PlayerEntity) {
                    PlayerEntity playerOwner = (PlayerEntity)owner;
                    if (!playerOwner.shouldDamagePlayer(player)) {
                        return false;
                    }
                }
            }

            return !(target instanceof CreeperEntity) && !(target instanceof GhastEntity);
        }
    }

    public boolean isSitting() {
        return (Boolean)this.dataTracker.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.spawnFeatherParticles(3);
            Entity entity = source.getAttacker();
            this.setSitting(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.damage(source, amount);
        }
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return MasonObjects.ENTITY_RAVEN_CAW;
    }

    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return MasonObjects.ENTITY_RAVEN_CAW;
    }

    public int tickTimer() {
        return this.age;
    }

    static {
        RECEIVER_UUID = DataTracker.registerData(RavenEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        TYPE = DataTracker.registerData(RavenEntity.class, TrackedDataHandlerRegistry.STRING);
        SITTING = DataTracker.registerData(RavenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        GOING_TO_RECEIVER = DataTracker.registerData(RavenEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }

    @Override
    public EntityView getEntityView() {
        return this.getWorld();
    }

    public static enum Type {
        DARK,
        ALBINO,
        SEA_GREEN,
        THREE_EYED;

        private Type() {
        }
    }
}

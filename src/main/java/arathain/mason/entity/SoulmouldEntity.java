package arathain.mason.entity;

import arathain.mason.entity.goal.SoulmouldAttackLogicGoal;
import arathain.mason.entity.goal.SoulmouldDashSlashGoal;
import arathain.mason.entity.goal.TamedAttackWithOwnerGoal;
import arathain.mason.entity.goal.TamedTrackAttackerGoal;
import arathain.mason.init.MasonObjects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Holder;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SoulmouldEntity extends HostileEntity implements TameableHostileEntity, GeoAnimatable {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    protected static final TrackedData<Boolean> DORMANT;
    public static final TrackedData<Optional<BlockPos>> DORMANT_POS;
    public static final TrackedData<Integer> ATTACK_STATE;
    public static final TrackedData<Integer> ACTION_STATE;
    public static final TrackedData<Direction> DORMANT_DIR;
    private static final TrackedData<Byte> TAMEABLE;
    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    public int activationTicks = 0;
    public int dashSlashTicks = 0;

    public SoulmouldEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.6F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    public static DefaultAttributeContainer.Builder createSoulmouldAttributes() {
        return MobEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 160.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ARMOR, 24.0).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    protected void initGoals() {
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(1, new SoulmouldAttackLogicGoal(this));
        this.goalSelector.add(0, new SoulmouldDashSlashGoal(this));
        this.targetSelector.add(1, new TamedTrackAttackerGoal(this));
        this.targetSelector.add(2, new TamedAttackWithOwnerGoal<>(this));
        this.targetSelector.add(2, new TargetGoal<>(this, LivingEntity.class, 10, true, false, (livingEntity) -> {
            boolean var10000;
            label48: {
                if (!livingEntity.equals(this.getOwner())) {
                    label41: {
                        if (livingEntity instanceof TameableEntity) {
                            TameableEntity tamed = (TameableEntity)livingEntity;
                            if (tamed.getOwner() != null && tamed.getOwner().equals(this.getOwner())) {
                                break label41;
                            }
                        }

                        if (!(livingEntity instanceof ArmorStandEntity)) {
                            label42: {
                                if (livingEntity instanceof SoulmouldEntity) {
                                    SoulmouldEntity mould = (SoulmouldEntity)livingEntity;
                                    if (mould.isOwner(this.getOwner())) {
                                        break label42;
                                    }
                                }

                                if (this.getActionState() == 2 && !(livingEntity instanceof BatEntity)) {
                                    if (!(livingEntity instanceof PlayerEntity)) {
                                        break label48;
                                    }

                                    PlayerEntity player = (PlayerEntity)livingEntity;
                                    if (!player.getUuid().equals(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2"))) {
                                        break label48;
                                    }
                                }
                            }
                        }
                    }
                }

                var10000 = false;
                return var10000;
            }

            var10000 = true;
            return var10000;
        }));
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_STATE, 0);
        this.dataTracker.startTracking(ACTION_STATE, 0);
        this.dataTracker.startTracking(DORMANT_POS, Optional.empty());
        this.dataTracker.startTracking(DORMANT_DIR, this.getHorizontalFacing());
        this.dataTracker.startTracking(DORMANT, true);
        this.dataTracker.startTracking(TAMEABLE, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return MasonObjects.ENTITY_SOULMOULD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return MasonObjects.ENTITY_SOULMOULD_DEATH;
    }

    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient() && source.isTypeIn(DamageTypeTags.IS_EXPLOSION)) {
            amount = (float)((double)amount * 0.5);
        }

        return super.damage(source, amount);
    }

    public boolean canFreeze() {
        return false;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        if (this.getDormantPos().isPresent()) {
            nbt.put("DormantPos", NbtHelper.fromBlockPos((BlockPos)this.getDormantPos().get()));
        }

        nbt.putInt("DormantDir", this.getDormantDir().getId());
        nbt.putInt("ActionState", this.getActionState());
        nbt.putInt("AttackState", this.getActionState());
        nbt.putInt("activationTicks", this.activationTicks);
        nbt.putInt("dashSlashTicks", this.dashSlashTicks);
        nbt.putBoolean("Dormant", this.isDormant());
    }

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

        if (nbt.contains("DormantPos")) {
            this.setDormantPos(NbtHelper.toBlockPos(nbt.getCompound("DormantPos")));
        }

        this.setActionState(nbt.getInt("ActionState"));
        this.setAttackState(nbt.getInt("AttackState"));
        this.setDormantDir(Direction.byId(nbt.getInt("DormantDir")));
        this.activationTicks = nbt.getInt("activationTicks");
        this.dashSlashTicks = nbt.getInt("dashSlashTicks");
        this.setDormant(nbt.getBoolean("Dormant"));
    }

    public UUID getOwnerUuid() {
        return (UUID)((Optional)this.dataTracker.get(OWNER_UUID)).orElse((Object)null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
    }

    public int getAttackState() {
        return (Integer)this.dataTracker.get(ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.dataTracker.set(ATTACK_STATE, state);
    }

    public boolean cannotDespawn() {
        return true;
    }

    public boolean isPersistent() {
        return true;
    }

    @Nullable
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

    public boolean isTamed() {
        return ((Byte)this.dataTracker.get(TAMEABLE) & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte b = (Byte)this.dataTracker.get(TAMEABLE);
        if (tamed) {
            this.dataTracker.set(TAMEABLE, (byte)(b | 4));
        } else {
            this.dataTracker.set(TAMEABLE, (byte)(b & -5));
        }

        this.onTamedChanged();
    }

    public void reset() {
        this.setTarget((LivingEntity)null);
        this.navigation.stop();
    }

    public ItemStack getPickBlockStack() {
        return MasonObjects.SOULMOULD_ITEM.getDefaultStack();
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isEmpty() && player.getUuid().equals(this.getOwnerUuid())) {
            if (player.isSneaking()) {
                if (!player.getAbilities().creativeMode) {
                    player.setStackInHand(hand, MasonObjects.SOULMOULD_ITEM.getDefaultStack());
                }

                this.remove(RemovalReason.DISCARDED);
                return ActionResult.SUCCESS;
            }

            this.cycleActionState(player);
        }

        return super.interactMob(player, hand);
    }

    private void cycleActionState(PlayerEntity player) {
        if (this.getActionState() == 0) {
            this.setActionState(2);
            player.sendMessage(Text.translatable("amogus", new Object[]{this.getWorld().getRegistryKey().getValue().getPath()}).setStyle(Style.EMPTY.withColor(Formatting.DARK_RED).withObfuscated(true).withFont(new Identifier("minecraft", "default"))), true);
        } else if (this.getActionState() == 2) {
            this.setActionState(1);
            player.sendMessage(Text.translatable("info.tot.mould_activate", new Object[]{this.getWorld().getRegistryKey().getValue().getPath()}).setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
        } else if (this.getActionState() == 1) {
            this.setActionState(0);
            player.sendMessage(Text.translatable("info.tot.mould_deactivate", new Object[]{this.getWorld().getRegistryKey().getValue().getPath()}).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), true);
        }

    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityTag) {
        this.setDormantPos(this.getBlockPos());
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    public int getActionState() {
        return (Integer)this.dataTracker.get(ACTION_STATE);
    }

    public void setActionState(int i) {
        this.dataTracker.set(ACTION_STATE, i);
    }

    public void tick() {
        super.tick();
        if (this.getAttackState() == 2 && this.age % 2 == 0) {
            ++this.dashSlashTicks;
        }

        if (this.dashSlashTicks >= 17) {
            this.setAttackState(0);
            this.dashSlashTicks = 0;
        }

        if (this.age % 5 == 0 && this.getHealth() < this.getMaxHealth() && this.isDormant()) {
            this.heal(2.0F);
        }

        if (this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget().getHealth() <= 0.0F)) {
            this.setTarget((LivingEntity)null);
        }

        if (!this.getWorld().isClient) {
            if (!this.isDormant()) {
                if ((this.getTarget() == null || this.getTarget() != null && this.getDormantPos().isPresent() && !((BlockPos)this.getDormantPos().get()).isWithinDistance(this.getTarget().getBlockPos(), 16.0)) && this.forwardSpeed == 0.0F && this.getNavigation().isIdle() && this.isAtDormantPos()) {
                    this.setDormant(true);
                    this.playSound(MasonObjects.ENTITY_SOULMOULD_AMBIENT, 1.0F, 1.0F);
                    this.updatePositionAndAngles((double)((BlockPos)this.getDormantPos().get()).getX() + 0.5, (double)((BlockPos)this.getDormantPos().get()).getY(), (double)((BlockPos)this.getDormantPos().get()).getZ() + 0.5, this.getYaw(), this.getPitch());
                }
            } else if (this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) < 100.0 && (Integer)this.dataTracker.get(ACTION_STATE) != 0) {
                if (this.activationTicks == 0) {
                    this.playSound(MasonObjects.ENTITY_SOULMOULD_AMBIENT, 1.0F, 1.0F);
                }

                ++this.activationTicks;
                this.setAttackState(1);
                if (this.activationTicks > 60) {
                    this.setDormant(false);
                    this.setAttackState(0);
                    this.activationTicks = 0;
                }
            }
        }

        if (this.isDormant()) {
            this.setVelocity(0.0, this.getVelocity().y, 0.0);
            this.setYaw(this.getDormantDir().asRotation());
            this.setBodyYaw(this.getDormantDir().asRotation());
            this.setHeadYaw(this.getDormantDir().asRotation());
            this.setPitch(0.0F);
        }

        if ((this.getTarget() == null || this.getTarget() != null && this.getDormantPos().isPresent() && !this.getTarget().isAlive()) && this.getNavigation().isIdle() && !this.isAtDormantPos() && !this.isDormant()) {
            this.updateDormantPos();
        }

    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        World var4 = this.getWorld();
        if (var4 instanceof ServerWorld server) {
            server.getServer().getPlayerManager().sendToAround((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), 32.0, server.getRegistryKey(), new PlaySoundFromEntityS2CPacket(Holder.createDirect(SoundEvents.ENTITY_IRON_GOLEM_STEP), this.getSoundCategory(), this, 32.0F, 1.0F, 69L));
        }

    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        RawAnimation animationBuilder = RawAnimation.begin();
        if (this.isDormant()) {
            if (this.getAttackState() == 1) {
                animationBuilder.thenPlay("activate");
            } else {
                animationBuilder.thenLoop("dormant");
            }
        } else if (this.getAttackState() == 2) {
            animationBuilder.thenLoop("attack");
        } else if (!this.hasVehicle() && event.isMoving()) {
            animationBuilder.thenLoop("walk");
        } else {
            animationBuilder.thenLoop("idle");
        }

        if (!animationBuilder.getAnimationStages().isEmpty()) {
            event.getController().setAnimation(animationBuilder);
        }

        return PlayState.CONTINUE;
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * 57.29577951308232 + 90.0;
    }

    public void pushAwayFrom(Entity entity) {
    }

    public boolean isCollidable() {
        return true;
    }

    public boolean collides() {
        return !this.isRemoved();
    }

    public Optional<BlockPos> getDormantPos() {
        return (Optional)this.getDataTracker().get(DORMANT_POS);
    }

    public void setDormantPos(BlockPos pos) {
        this.getDataTracker().set(DORMANT_POS, Optional.of(pos));
    }

    private boolean isAtDormantPos() {
        Optional<BlockPos> restPos = this.getDormantPos();
        return restPos.isPresent() ? ((BlockPos)restPos.get()).isWithinDistance(this.getBlockPos(), 1.600000023841858) : false;
    }

    private void updateDormantPos() {
        boolean reassign = true;
        if (this.getDormantPos().isPresent()) {
            BlockPos pos = (BlockPos)this.getDormantPos().get();
            if (this.getNavigation().startMovingTo((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.7)) {
                reassign = false;
            }

            reassign = false;
        }

        if (reassign) {
            this.setDormantPos(this.getBlockPos());
        }

    }

    public Direction getDormantDir() {
        return (Direction)this.getDataTracker().get(DORMANT_DIR);
    }

    public void setDormantDir(Direction dir) {
        this.getDataTracker().set(DORMANT_DIR, dir);
    }

    public boolean isDormant() {
        return (Boolean)this.getDataTracker().get(DORMANT);
    }

    public void setDormant(boolean rest) {
        this.getDataTracker().set(DORMANT, rest);
    }

    static {
        DORMANT = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DORMANT_POS = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
        ATTACK_STATE = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ACTION_STATE = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.INTEGER);
        DORMANT_DIR = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.DIRECTION);
        TAMEABLE = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.BYTE);
        OWNER_UUID = DataTracker.registerData(SoulmouldEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public double getTick(Object object) {
        return this.age;
    }
}
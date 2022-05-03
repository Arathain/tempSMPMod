package arathain.mason.entity;

import arathain.mason.entity.goal.TamedAttackWithOwnerGoal;
import arathain.mason.entity.goal.TamedTrackAttackerGoal;
import com.google.common.collect.ImmutableList;
import gg.moonflower.mannequins.common.entity.AbstractMannequin;
import gg.moonflower.mannequins.common.entity.Statue;
import gg.moonflower.mannequins.core.registry.MannequinsEntities;
import gg.moonflower.mannequins.core.registry.MannequinsItems;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class AnimatedStatueEntity extends HostileEntity implements TameableHostileEntity {
    public static final TrackedData<EulerAngle> DATA_HEAD_POSE = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> DATA_BODY_POSE = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> DATA_LEFT_ARM_POSE = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> DATA_RIGHT_ARM_POSE = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<Boolean> DATA_DISABLED = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> DATA_TROLLED = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> DATA_YAW = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<OptionalInt> DATA_EXPRESSION = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.FIREWORK_DATA);
    private static final EulerAngle DEFAULT_HEAD_POSE = new EulerAngle(0.0F, 0.0F, 0.0F);
    private static final EulerAngle DEFAULT_BODY_POSE = new EulerAngle(0.0F, 0.0F, 0.0F);
    private static final EulerAngle DEFAULT_LEFT_ARM_POSE = new EulerAngle(-10.0F, 0.0F, -10.0F);
    private static final EulerAngle DEFAULT_RIGHT_ARM_POSE = new EulerAngle(-10.0F, 0.0F, 10.0F);
    private EulerAngle headPose = DEFAULT_HEAD_POSE;
    private EulerAngle bodyPose = DEFAULT_BODY_POSE;
    private EulerAngle leftArmPose = DEFAULT_LEFT_ARM_POSE;
    private EulerAngle rightArmPose = DEFAULT_RIGHT_ARM_POSE;
    private final SimpleInventory inventory = new SimpleInventory(4);
    public static final TrackedData<Optional<BlockPos>> DORMANT_POS = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    private static final TrackedData<Byte> TAMEABLE = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(AnimatedStatueEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public AnimatedStatueEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
    }
    public static DefaultAttributeContainer.Builder createStatueAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f).add(EntityAttributes.GENERIC_ARMOR, 10f);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
        this.dataTracker.startTracking(DATA_BODY_POSE, DEFAULT_BODY_POSE);
        this.dataTracker.startTracking(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
        this.dataTracker.startTracking(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
        this.dataTracker.startTracking(DATA_EXPRESSION, OptionalInt.empty());
        this.dataTracker.startTracking(DATA_DISABLED, false);
        this.dataTracker.startTracking(DATA_YAW, 0f);
        this.dataTracker.startTracking(DATA_TROLLED, false);
        this.dataTracker.startTracking(DORMANT_POS, Optional.empty());
        this.dataTracker.startTracking(TAMEABLE, (byte) 0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        if(getDormantPos().isPresent()) {
            nbt.put("DormantPos", NbtHelper.fromBlockPos(getDormantPos().get()));
        }
        nbt.put("Pose", this.writePose());
        this.getExpression().ifPresent(expression -> nbt.putInt("Expression", expression.ordinal()));
        nbt.putBoolean("Trolled", this.isTrolled());
        nbt.putBoolean("Disabled", this.isDisabled());
        nbt.putFloat("DormantYaw", this.dataTracker.get(DATA_YAW));
        NbtList listTag = new NbtList();

        for(int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound compoundTag2 = new NbtCompound();
                compoundTag2.putByte("Slot", (byte)i);
                itemStack.writeNbt(compoundTag2);
                listTag.add(compoundTag2);
            }
        }

        nbt.put("Items", listTag);
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
        if(nbt.contains("DormantPos")) {
            setDormantPos(NbtHelper.toBlockPos(nbt.getCompound("DormantPos")));
        }
        this.readPose(nbt.getCompound("Pose"));
        if (nbt.contains("Expression", 3)) {
            this.setExpression(AbstractMannequin.Expression.byId(nbt.getInt("Expression")));
        }

        this.setTrolled(nbt.getBoolean("Trolled"));
        this.dataTracker.set(DATA_YAW, nbt.getFloat("DormantYaw"));
        this.dataTracker.set(DATA_DISABLED, nbt.getBoolean("Disabled"));
        if (nbt.contains("Items", 9)) {
            NbtList listTag = nbt.getList("Items", 10);

            for(int i = 0; i < listTag.size(); ++i) {
                NbtCompound compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                if (j < this.inventory.size()) {
                    this.inventory.setStack(j, ItemStack.fromNbt(compoundTag2));
                }
            }
        }
    }
    private void readPose(NbtCompound tag) {
        NbtList headPose = tag.getList("Head", 5);
        this.setHeadPose(headPose.isEmpty() ? DEFAULT_HEAD_POSE : new EulerAngle(headPose));
        NbtList bodyPose = tag.getList("Body", 5);
        this.setBodyPose(bodyPose.isEmpty() ? DEFAULT_BODY_POSE : new EulerAngle(bodyPose));
        NbtList leftArmPose = tag.getList("LeftArm", 5);
        this.setLeftArmPose(leftArmPose.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new EulerAngle(leftArmPose));
        NbtList rightArmPose = tag.getList("RightArm", 5);
        this.setRightArmPose(rightArmPose.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new EulerAngle(rightArmPose));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, false));
        this.targetSelector.add(1, new TamedTrackAttackerGoal(this));
        this.targetSelector.add(2, new TamedAttackWithOwnerGoal<>(this));
    }

    private NbtCompound writePose() {
        NbtCompound pose = new NbtCompound();
        if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
            pose.put("Head", this.headPose.toNbt());
        }

        if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
            pose.put("Body", this.bodyPose.toNbt());
        }

        if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
            pose.put("LeftArm", this.leftArmPose.toNbt());
        }

        if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
            pose.put("RightArm", this.rightArmPose.toNbt());
        }

        return pose;
    }
    public Optional<AbstractMannequin.Expression> getExpression() {
        OptionalInt id = (OptionalInt)this.dataTracker.get(DATA_EXPRESSION);
        return !id.isPresent() ? Optional.empty() : Optional.of(AbstractMannequin.Expression.byId(id.getAsInt()));
    }

    public void setExpression(@org.jetbrains.annotations.Nullable AbstractMannequin.Expression expression) {
        this.dataTracker.set(DATA_EXPRESSION, expression == null ? OptionalInt.empty() : OptionalInt.of(expression.ordinal()));
    }

    public boolean isTrolled() {
        return this.dataTracker.get(DATA_TROLLED);
    }

    public void setTrolled(boolean trolled) {
        this.dataTracker.set(DATA_TROLLED, trolled);
    }

    public boolean isDisabled() {
        return this.dataTracker.get(DATA_DISABLED);
    }

    public EulerAngle getHeadPose() {
        return this.headPose;
    }

    public void setHeadPose(EulerAngle pose) {
        this.headPose = pose;
        this.dataTracker.set(DATA_HEAD_POSE, pose);
    }

    public EulerAngle getBodyPose() {
        return this.bodyPose;
    }

    public void setBodyPose(EulerAngle pose) {
        this.bodyPose = pose;
        this.dataTracker.set(DATA_BODY_POSE, pose);
    }

    public EulerAngle getLeftArmPose() {
        return this.leftArmPose;
    }

    public void setLeftArmPose(EulerAngle pose) {
        this.leftArmPose = pose;
        this.dataTracker.set(DATA_LEFT_ARM_POSE, pose);
    }

    public EulerAngle getRightArmPose() {
        return this.rightArmPose;
    }

    public void setRightArmPose(EulerAngle pose) {
        this.rightArmPose = pose;
        this.dataTracker.set(DATA_RIGHT_ARM_POSE, pose);
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
        return (this.dataTracker.get(TAMEABLE) & 4) != 0;
    }

    @Override
    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE);
        if (tamed) {
            this.dataTracker.set(TAMEABLE, (byte) (b | 4));
        } else {
            this.dataTracker.set(TAMEABLE, (byte) (b & -5));
        }

        this.onTamedChanged();
    }
    public void reset() {
        this.setTarget(null);
        this.navigation.stop();
    }
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityTag) {
        setDormantPos(getBlockPos());
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }
    public Iterable<ItemStack> getItemsHand() {
        return ImmutableList.of(this.inventory.getStack(2), this.inventory.getStack(3));
    }

    public Iterable<ItemStack> getArmorItems() {
        return ImmutableList.of(this.inventory.getStack(0), this.inventory.getStack(1));
    }

    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == null) {
            return ItemStack.EMPTY;
        } else {
            switch(slot) {
                case HEAD:
                    return this.inventory.getStack(0);
                case CHEST:
                    return this.inventory.getStack(1);
                case MAINHAND:
                    return this.inventory.getStack(2);
                case OFFHAND:
                    return this.inventory.getStack(3);
                default:
                    return ItemStack.EMPTY;
            }
        }
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        if (slot != null) {
            switch(slot) {
                case HEAD:
                    this.inventory.setStack(0, stack);
                    break;
                case CHEST:
                    this.inventory.setStack(1, stack);
                    break;
                case MAINHAND:
                    this.inventory.setStack(2, stack);
                    break;
                case OFFHAND:
                    this.inventory.setStack(3, stack);
            }

        }
    }
    public void setDormantYaw(float yaw) {
        this.dataTracker.set(DATA_YAW, yaw);
    }
    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && (!getTarget().isAlive() || getTarget().getHealth() <= 0)) setTarget(null);
        if(!world.isClient) {
            if ((this.getTarget() == null || (this.getTarget() != null && this.getDormantPos().isPresent() && !this.getDormantPos().get().isWithinDistance(this.getTarget().getPos(), 64))) && forwardSpeed == 0 && this.getNavigation().isIdle() && isAtDormantPos() && this.age > 300) {
                Statue statue = new Statue(MannequinsEntities.STATUE.get(), world);
                if(this.getExpression().isPresent()) {
                    statue.setExpression(this.getExpression().get());
                }
                statue.setBodyPose(this.getBodyPose());
                statue.setHeadPose(this.getHeadPose());
                statue.setTrolled(this.isTrolled());
                statue.setLeftArmPose(this.getLeftArmPose());
                statue.setRightArmPose(this.getRightArmPose());
                statue.equipStack(EquipmentSlot.HEAD, this.getEquippedStack(EquipmentSlot.HEAD));
                statue.equipStack(EquipmentSlot.MAINHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
                statue.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.OFFHAND));
                statue.equipStack(EquipmentSlot.CHEST, this.getEquippedStack(EquipmentSlot.CHEST));
                statue.updatePositionAndAngles(getDormantPos().get().getX() + 0.5, getDormantPos().get().getY(), getDormantPos().get().getZ() + 0.5, this.dataTracker.get(DATA_YAW), 0);
                world.spawnEntity(statue);
                this.remove(RemovalReason.DISCARDED);
            }
        }
        if ((this.getTarget() == null || (this.getTarget() != null && this.getDormantPos().isPresent() && !this.getDormantPos().get().isWithinDistance(this.getTarget().getPos(), 20))) && getNavigation().isIdle() && !isAtDormantPos()) updateDormantPos();
    }

    public Optional<BlockPos> getDormantPos() {
        return getDataTracker().get(DORMANT_POS);
    }
    @Override
    public void pushAwayFrom(Entity entity) {

    }
    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    private void breakMannequin(DamageSource source) {
        this.drop(source);

        for(int i = 0; i < this.inventory.size(); ++i) {
            ItemStack stack = this.inventory.getStack(i);
            if (!stack.isEmpty()) {
                Block.dropStack(this.world, this.getBlockPos().up(), stack);
                this.inventory.setStack(i, ItemStack.EMPTY);
            }
        }

    }

    @Override
    public void onDeath(DamageSource source) {
        Block.dropStack(this.world, this.getBlockPos(), MannequinsItems.STATUE.get().getDefaultStack());
        this.breakMannequin(source);
        super.onDeath(source);
    }

    public void setDormantPos(BlockPos pos) {
        getDataTracker().set(DORMANT_POS, Optional.of(pos));
    }
    private boolean isAtDormantPos() {
        Optional<BlockPos> restPos = getDormantPos();
        if(restPos.isPresent()) {
            return restPos.get().isWithinDistance(this.getBlockPos(), 1.6f);
        }
        return false;
    }

    private void updateDormantPos() {
        boolean reassign = true;
        if (getDormantPos().isPresent()) {
            BlockPos pos = getDormantPos().get();
            if (this.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.7)) {
                reassign = false;
            }
            reassign = false;
        }
        if (reassign) {
            setDormantPos(getBlockPos());
        }
    }
}

package arathain.mason.entity.goal;

import arathain.mason.entity.RavenEntity;
import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class RavenDeliverBundleGoal<T extends TameableEntity> extends Goal {
    private final T tameable;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;
    private LivingEntity receiver;

    public RavenDeliverBundleGoal(T tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.tameable = tameable;
        this.world = tameable.getWorld();
        this.speed = speed;
        this.navigation = tameable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(tameable.getNavigation() instanceof MobNavigation) && !(tameable.getNavigation() instanceof BirdNavigation) || !(tameable instanceof RavenEntity)) {
            throw new IllegalArgumentException("Unsupported mob type for DeliverBundleGoal");
        }
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (((RavenEntity)this.tameable).getReceiverUuid() != null) {
            this.receiver = this.tameable.getWorld().getPlayerByUuid(((RavenEntity)this.tameable).getReceiverUuid());
            LivingEntity receiver = this.receiver;
            if (this.receiver != null) {
                if (livingEntity == null) {
                    return false;
                } else if (livingEntity.isSpectator()) {
                    return false;
                } else if (this.tameable.squaredDistanceTo(receiver) < (double)(this.minDistance * this.minDistance)) {
                    return false;
                } else {
                    this.owner = livingEntity;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } else {
            return this.tameable.squaredDistanceTo(this.receiver) > (double)(this.maxDistance * this.maxDistance);
        }
    }

    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.tameable.getDataTracker().set(RavenEntity.GOING_TO_RECEIVER, true);
    }

    public void stop() {
        this.receiver = null;
        this.navigation.stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    public void tick() {
        this.tameable.getLookControl().lookAt(this.receiver, 10.0F, (float)this.tameable.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (this.tameable.squaredDistanceTo(this.receiver) >= 10000.0) {
                this.tryTeleport();
            } else {
                this.navigation.startMovingTo(this.receiver, this.speed);
            }
        }

    }

    private void tryTeleport() {
        BlockPos blockPos = this.receiver.getBlockPos();
        ((RavenEntity)this.tameable).spawnFeatherParticles(10);

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) {
                return;
            }
        }

    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.receiver.getX()) < 2.0 && Math.abs((double)z - this.receiver.getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.tameable.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.tameable.headYaw, this.tameable.getPitch());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.down());
            if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.tameable.getBlockPos());
                return this.world.isSpaceEmpty(this.tameable, this.tameable.getBoundingBox().offset(blockPos));
            }
        }
    }

    private int getRandomInt(int min, int max) {
        return this.tameable.getRandom().nextInt(max - min + 1) + min;
    }
}

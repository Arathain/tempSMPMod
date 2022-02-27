package arathain.mason.entity.goal;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class SoulmouldAttackLogicGoal extends Goal {
    private final SoulmouldEntity mould;
    private int scrunkly;
    private double targetX;
    private double targetY;
    private double targetZ;

    public SoulmouldAttackLogicGoal(SoulmouldEntity entity) {
        this.mould = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }
    @Override
    public boolean canStart() {
        LivingEntity target = this.mould.getTarget();
        return target != null && target.isAlive() && !this.mould.isDormant();
    }
    @Override
    public void start() {
        this.scrunkly = 0;
    }

    @Override
    public void stop() {
        this.mould.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mould.getTarget();
        if(target == null) return;
        double distance = this.mould.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
        if (--this.scrunkly <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || target.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D) || this.mould.getNavigation().isIdle()) {
            this.targetX = target.getX();
            this.targetY = target.getY();
            this.targetZ = target.getZ();
            this.scrunkly = 4 + this.mould.getRandom().nextInt(6);
            if (distance > 32.0D * 32.0D) {
                this.scrunkly += 10;
            } else if (distance > 16.0D * 16.0D) {
                this.scrunkly += 5;
            }
            if (!this.mould.getNavigation().startMovingTo(target, 0.5D)) {
                this.scrunkly += 15;
            }
        }
        distance = this.mould.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
        if (target.getY() - this.mould.getY() >= -1 && target.getY() - this.mould.getY() <= 3) {
            //distance < 3.5D * 3.5D &&
            if (Math.abs(MathHelper.wrapDegrees(this.mould.getAngleBetweenEntities(target, this.mould) - this.mould.getYaw())) < 35.0D) {
                this.mould.setAttackState(2);
                this.mould.dashSlashTicks = 0;
            }
        }
    }

}

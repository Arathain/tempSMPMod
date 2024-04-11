package arathain.mason.entity.goal;

import arathain.mason.entity.TameableHostileEntity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.mob.MobEntity;

public class TamedAttackWithOwnerGoal<T extends TameableHostileEntity> extends TrackTargetGoal {
    private final T tamed;
    private LivingEntity attacking;
    private int lastAttackTime;

    public TamedAttackWithOwnerGoal(T tamed) {
        super((MobEntity)tamed, false);
        this.tamed = tamed;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        if (this.tamed.isTamed()) {
            LivingEntity livingEntity = this.tamed.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getAttacking();
                int i = livingEntity.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tamed.canAttackWithOwner(this.attacking, livingEntity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tamed.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }
}
package arathain.mason.entity.goal;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class SoulmouldDashSlashGoal extends Goal {
    private final SoulmouldEntity mould;
    public SoulmouldDashSlashGoal(SoulmouldEntity entity) {
        this.mould = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return this.mould.getAttackState() == 2 && !this.mould.isDormant() && mould.getTarget() != null;
    }

    @Override
    public void tick() {
        int ticks = mould.dashSlashTicks;
        LivingEntity target = mould.getTarget();
        this.mould.lookAtEntity(this.mould.getTarget(), 80, 80);
        if(ticks == 9 || ticks == 12 || ticks == 15) {
            Vec3d vec3d = this.mould.getVelocity();
            Vec3d vec3d2 = new Vec3d(target.getX() - mould.getX(), 0.0, target.getZ() - this.mould.getZ());
            vec3d2 = vec3d2.normalize().multiply(1).add(vec3d);

            this.mould.setVelocity(vec3d2.x, (double)0, vec3d2.z);
        }
        if(ticks == 10 || ticks == 13 || ticks == 15) {
            List<LivingEntity> entities = mould.getWorld().getEntitiesByClass(LivingEntity.class, mould.getBoundingBox().expand(4, 3, 4), livingEntity -> livingEntity != mould && livingEntity != mould.getOwner() && mould.distanceTo(livingEntity) <= 4 + livingEntity.getWidth() / 2 && livingEntity.getY() <= mould.getY() + 3);
            for(LivingEntity entity: entities) {
                Vec3d vec = entity.getPos().subtract(mould.getPos()).normalize().negate();
                entity.takeKnockback(1, vec.x, vec.z);
                entity.damage(DamageSource.mob(mould), 10);
            }
        }
    }
}

package arathain.mason.entity.goal;

import arathain.mason.entity.SoulmouldEntity;
import arathain.mason.init.MasonObjects;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.util.math.Vec3d;

public class SoulmouldDashSlashGoal extends Goal {
    private final SoulmouldEntity mould;

    public SoulmouldDashSlashGoal(SoulmouldEntity entity) {
        this.mould = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    public boolean canStart() {
        return this.mould.getAttackState() == 2 && !this.mould.isDormant() && this.mould.getTarget() != null;
    }

    public void tick() {
        int ticks = this.mould.dashSlashTicks;
        LivingEntity target = this.mould.getTarget();
        this.mould.lookAtEntity(this.mould.getTarget(), 80.0F, 80.0F);
        if (ticks == 9 || ticks == 12 || ticks == 15) {
            Vec3d vec3d = this.mould.getVelocity();
            Vec3d vec3d2 = new Vec3d(target.getX() - this.mould.getX(), 0.0, target.getZ() - this.mould.getZ());
            vec3d2 = vec3d2.normalize().multiply(1.0).add(vec3d);
            this.mould.setVelocity(vec3d2.x, 0.0, vec3d2.z);
        }

        if (ticks == 10 || ticks == 13 || ticks == 15) {
            this.mould.playSound(MasonObjects.ENTITY_SOULMOULD_ATTACK, 1.0F, 1.0F);
            List<LivingEntity> entities = this.mould.getWorld().getEntitiesByClass(LivingEntity.class, this.mould.getBoundingBox().expand(4.0, 3.0, 4.0), (livingEntity) -> {
                boolean var10000;
                if (livingEntity != this.mould && livingEntity != this.mould.getOwner()) {
                    label19: {
                        if (livingEntity instanceof SoulmouldEntity) {
                            SoulmouldEntity smould = (SoulmouldEntity)livingEntity;
                            if (smould.getOwner() == this.mould.getOwner()) {
                                break label19;
                            }
                        }

                        if (this.mould.distanceTo(livingEntity) <= 4.0F + livingEntity.getWidth() / 2.0F && livingEntity.getY() <= this.mould.getY() + 3.0) {
                            var10000 = true;
                            return var10000;
                        }
                    }
                }

                var10000 = false;
                return var10000;
            });
            Iterator var8 = entities.iterator();

            while(var8.hasNext()) {
                LivingEntity entity = (LivingEntity)var8.next();
                Vec3d vec = entity.getPos().subtract(this.mould.getPos()).normalize().negate();
                entity.takeKnockback(1.0, vec.x, vec.z);
                this.mould.tryAttack(entity);
            }
        }

    }
}


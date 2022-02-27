package arathain.mason.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface TameableHostileEntity {
    UUID getOwnerUuid();
    void setOwnerUuid(@Nullable UUID uuid);
    void setOwner(PlayerEntity player);
    LivingEntity getOwner();
    boolean isOwner(LivingEntity entity);

    boolean isTamed();
    void setTamed(boolean tamed);

    default void onTamedChanged(){

    }

    default boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof TameableHostileEntity) {
                TameableHostileEntity entity = (TameableHostileEntity)target;
                return !entity.isTamed() || entity.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
                return false;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
            }
        } else {
            return false;
        }
    }
    default void reset() {

    }
}
package arathain.mason.mixin;

import arathain.mason.entity.BoneflyEntity;
import arathain.mason.entity.ChainsEntity;
import arathain.mason.entity.SoulExplosionEntity;
import arathain.mason.init.MasonObjects;
import arathain.mason.item.GlaiveItem;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow protected boolean isSubmergedInWater;

    @Unique
    private int soultrapTicks = 0;

    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract boolean isInvulnerableTo(DamageSource damageSource);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    private boolean isCorrectDamageSource(DamageSource source) {
        return (source instanceof EntityDamageSource esource && esource.getAttacker() instanceof PlayerEntity) || (source instanceof ProjectileDamageSource psource && psource.getAttacker() != null && psource.getAttacker() instanceof PlayerEntity) || source.isFromFalling() || soultrapTicks > 0;
    }

    @Override
    protected boolean tryUseTotem(DamageSource source) {
        //TODO the false is temp
        if (this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack()) && isCorrectDamageSource(source) && !(soultrapTicks >= 18) && false) {
            if(soultrapTicks == 0) {
                ChainsEntity chains = new ChainsEntity(MasonObjects.CHAINS, this.getWorld());
                chains.setPosition(this.getPos().add(0, 0.3, 0));
                this.getWorld().spawnEntity(chains);
                this.startRiding(chains, true);
            }
            this.setHealth(1.0F);
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 400, 2, true, false));
            ++this.soultrapTicks;
            return true;
        } else {
            if(soultrapTicks >= 18) {
                this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 6.0f, Explosion.DestructionType.NONE);
                SoulExplosionEntity entity = new SoulExplosionEntity(MasonObjects.SOUL_EXPLOSION, this.world);
                entity.setPosition(this.getPos());
                this.world.spawnEntity(entity);
            }
            return super.tryUseTotem(source);
        }
    }


    @Inject(method = "shouldDismount", at = @At("HEAD"), cancellable = true)
    private void webbingScuffedry(CallbackInfoReturnable<Boolean> cir) {
        if((this.getVehicle() instanceof BoneflyEntity && !this.getVehicle().getFirstPassenger().equals(this)) || this.getVehicle() instanceof ChainsEntity) {
            cir.setReturnValue(false);
        }
    }
    @Override
    public boolean isSneaking() {
        if(this.getVehicle() instanceof ChainsEntity) {
            return false;
        }
        return super.isSneaking();
    }
    @Inject(method = "damage", at = @At("HEAD"))
    private void submergedDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack()) && this.isSubmergedInWater && !isInvulnerableTo(source) && !this.isDead() && random.nextInt(6) == 1) {
            super.damage(source, amount);
            this.timeUntilRegen = 0;
        }
    }

    @Override
    public EntityGroup getGroup() {
        if(this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack())) {
            return EntityGroup.UNDEAD;
        }
        return super.getGroup();
    }
    @Override
    public boolean isUndead() {
        if(this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack())) {
            return true;
        }
        return super.isUndead();
    }

    @Override
    public boolean hurtByWater() {
        if(this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack()) && (this.getWorld().getBiome(this.getBlockPos()).isIn(BiomeTags.IS_RIVER) || isInFlowingFluid(FluidTags.WATER))) {
            return true;
        }
        return super.hurtByWater();
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if(this.getInventory().contains(MasonObjects.SOULTRAP_EFFIGY_ITEM.getDefaultStack())) {
            return effect.getEffectType() == StatusEffects.WITHER || effect.getEffectType() == StatusEffects.INSTANT_DAMAGE || effect.getEffectType() == StatusEffects.INSTANT_HEALTH;
        }
        return super.canHaveStatusEffect(effect);
    }
    private boolean isInFlowingFluid(TagKey<Fluid> tag) {
        if (this.isRegionUnloaded()) {
            return false;
        }
        Box box = this.getBoundingBox().contract(0.001);
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        double d = 0.0;
        boolean bl = this.isPushedByFluids();
        boolean bl2 = false;
        Vec3d vec3d = Vec3d.ZERO;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int p = i; p < j; ++p) {
            for (int q = k; q < l; ++q) {
                for (int r = m; r < n; ++r) {
                    double e;
                    mutable.set(p, q, r);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (!fluidState.isIn(tag) || !((e = (double)((float)q + fluidState.getHeight(this.world, mutable))) >= box.minY) || fluidState.isStill()) continue;
                    bl2 = true;
                    d = Math.max(e - box.minY, d);
                    if (!bl) continue;
                    Vec3d vec3d2 = fluidState.getVelocity(this.world, mutable);
                    if (d < 0.4) {
                        vec3d2 = vec3d2.multiply(d);
                    }
                    vec3d = vec3d.add(vec3d2);
                    ++o;
                }
            }
        }
        return bl2;
    }
}

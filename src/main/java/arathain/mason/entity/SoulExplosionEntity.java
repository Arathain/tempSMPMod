package arathain.mason.entity;

import arathain.mason.init.MasonObjects;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SoulExplosionEntity extends Entity {

    public SoulExplosionEntity(EntityType<SoulExplosionEntity> soulExplosionEntityEntityType, World world) {
        super(MasonObjects.SOUL_EXPLOSION, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient()) {
            for (int i = 1; i < (8); i++) {
                this.getEntityWorld().addImportantParticle(ParticleTypes.LARGE_SMOKE,
                        this.getX() + random.nextGaussian(),
                        this.getY(),
                        this.getZ() + random.nextGaussian(),
                        0, 0.1, 0);
                this.getEntityWorld().addImportantParticle(ParticleTypes.LARGE_SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        MathHelper.cos(i/4f * 3.141592f + (this.age + random.nextFloat()) / 10f) / 2f, 0.2, MathHelper.sin(i/4f * 3.141592f + (this.age + random.nextFloat()) / 10f) / 2f);
                this.getEntityWorld().addImportantParticle(ParticleTypes.LARGE_SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        MathHelper.cos(i/4f * 3.141592f + (this.age + random.nextFloat()) / 10f) / 2f, 0.2, MathHelper.sin(i/4f * 3.141592f + (this.age + random.nextFloat()) / 10f) / 2f);
                this.getEntityWorld().addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        (random.nextFloat()-0.5f) / 4, 1f + random.nextFloat(), (random.nextFloat()-0.5f) / 4);
                this.getEntityWorld().addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        (random.nextFloat()-0.5f) / 4, 1f + random.nextFloat(), (random.nextFloat()-0.5f) / 4);
                this.getEntityWorld().addImportantParticle(ParticleTypes.SOUL,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        (random.nextFloat()-0.5f) / 4, 3f + random.nextFloat(), (random.nextFloat()-0.5f) / 4);
            }
        } else {
            this.world.createExplosion(this, this.getX() + random.nextGaussian() * 4, this.getY() - MathHelper.abs((float) random.nextGaussian()) * 20 + 10, this.getZ() + random.nextGaussian() * 4, 7.0f, true, Explosion.DestructionType.DESTROY);
            if(this.age > 600) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

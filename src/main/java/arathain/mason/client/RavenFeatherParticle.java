package arathain.mason.client;

import net.minecraft.client.particle.AbstractSlowingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class RavenFeatherParticle extends AbstractSlowingParticle {
    private RavenFeatherParticle(ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
        super(world, x, y, z, velX, velY, velZ);
        this.scale(0.7F + (float)world.random.nextInt(6) / 10.0F);
        this.angle = this.prevAngle = this.random.nextFloat() * 6.2831855F;
        this.velocityY = -0.25;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 12;
    }

    public void tick() {
        if (this.age > this.maxAge / 2) {
            this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
        }

        this.angle = (float)((double)this.prevAngle + Math.sin((double)((float)this.age / 20.0F)) / 10.0);
        super.tick();
        if (this.age == 1) {
            this.velocityX += (Math.random() * 2.0 - 1.0) * 0.05;
            this.velocityY = 0.1 + (double)this.random.nextInt(11) / 100.0;
            this.velocityZ += (Math.random() * 2.0 - 1.0) * 0.05;
        } else if (this.age <= 10) {
            this.velocityY -= 0.04;
        }

        if (this.onGround) {
            this.setVelocity(0.0, 0.0, 0.0);
            this.setPos(this.prevPosX, this.prevPosY + 0.1, this.prevPosZ);
        }

    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static record Factory(SpriteProvider sprites) implements ParticleFactory<DefaultParticleType> {
        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(DefaultParticleType simpleParticleType, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ) {
            RavenFeatherParticle particle = new RavenFeatherParticle(world, x, y, z, velX, velY, velZ);
            particle.setSpriteForAge(this.sprites);
            return particle;
        }

        public SpriteProvider sprites() {
            return this.sprites;
        }
    }
}

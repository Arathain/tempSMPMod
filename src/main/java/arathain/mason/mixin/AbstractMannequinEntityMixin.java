package arathain.mason.mixin;

import gg.moonflower.mannequins.common.entity.AbstractMannequin;
import gg.moonflower.mannequins.common.entity.Statue;
import gg.moonflower.mannequins.core.registry.MannequinsItems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractMannequin.class)
public abstract class AbstractMannequinEntityMixin extends LivingEntity {
    @Shadow protected abstract void breakMannequin(DamageSource source);

    @Shadow public abstract ItemStack getItem();

    protected AbstractMannequinEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void remove(RemovalReason reason) {
        if(reason.shouldDestroy()) {
            this.onDeath(DamageSource.MAGIC);
        }
        super.remove(reason);
    }

    @Override
    public void onDeath(DamageSource source) {
        Block.dropStack(this.world, this.getBlockPos(), getItem());
        this.breakMannequin(source);
    }
}

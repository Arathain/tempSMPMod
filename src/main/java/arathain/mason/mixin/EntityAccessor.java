package arathain.mason.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("streamIntoPassengers")
    public Stream<Entity> mason$streamIntoPassengers();
}

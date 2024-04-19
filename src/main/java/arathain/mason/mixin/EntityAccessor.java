package arathain.mason.mixin;

import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Entity.class})
public interface EntityAccessor {
    @Invoker("streamIntoPassengers")
    Stream<Entity> mason$streamIntoPassengers();
}

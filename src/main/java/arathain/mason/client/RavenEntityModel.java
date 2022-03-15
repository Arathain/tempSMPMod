
package arathain.mason.client;
import arathain.mason.entity.RavenEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Overwrite;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class RavenEntityModel extends AnimatedTickingGeoModel<RavenEntity> {
    @Override
    public Identifier getModelLocation(RavenEntity object) {
        return new Identifier("tot", "geo/entity/raven.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RavenEntity object) {
        return new Identifier("tot", "textures/entity/raven/raven_"+ object.getRavenType().toString().toLowerCase() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(RavenEntity animatable) {
        return new Identifier("tot", "animations/entity/raven.animation.json");
    }

    @Override
    public void codeAnimations(RavenEntity entity, Integer uniqueID, AnimationEvent<?> customPredicate) {
        super.codeAnimations(entity, uniqueID, customPredicate);
        IBone root = this.getAnimationProcessor().getBone("root");
        if(entity.isBaby()) {
            if (root != null) {
                root.setScaleX(0.5f);
                root.setScaleY(0.5f);
                root.setScaleZ(0.5f);
                root.setPositionY(-0.1F);
            }
        } else {
            if (root != null) {
                root.setScaleX(1.2f);
                root.setScaleY(1.2f);
                root.setScaleZ(1.2f);
                root.setPositionY(0.05F);
            }
        }
    }
}

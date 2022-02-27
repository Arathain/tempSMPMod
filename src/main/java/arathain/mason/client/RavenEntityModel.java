
package arathain.mason.client;
import arathain.mason.entity.RavenEntity;
import net.minecraft.util.Identifier;
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
}

package arathain.mason.client;

import arathain.mason.entity.ChainsEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChainsEntityModel extends AnimatedGeoModel<ChainsEntity> {
    @Override
    public Identifier getModelLocation(ChainsEntity object) {
        return new Identifier("tot", "geo/entity/chains.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ChainsEntity object) {
        return new Identifier("tot", "textures/entity/chains.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ChainsEntity animatable) {
        return new Identifier("tot", "animations/entity/chains.animation.json");
    }
}

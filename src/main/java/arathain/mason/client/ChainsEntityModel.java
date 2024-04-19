package arathain.mason.client;

import arathain.mason.MasonDecor;
import arathain.mason.entity.ChainsEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChainsEntityModel extends GeoModel<ChainsEntity> {
    @Override
    public Identifier getModelResource(ChainsEntity object) {
        return new Identifier(MasonDecor.MODID, "geo/entity/chains.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChainsEntity object) {
        return new Identifier(MasonDecor.MODID, "textures/entity/chains.png");
    }

    @Override
    public Identifier getAnimationResource(ChainsEntity animatable) {
        return new Identifier(MasonDecor.MODID, "animations/entity/chains.animation.json");
    }
}

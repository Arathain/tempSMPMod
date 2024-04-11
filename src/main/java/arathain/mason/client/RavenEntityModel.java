package arathain.mason.client;

import arathain.mason.entity.RavenEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.GeoModel;

public class RavenEntityModel extends GeoModel<RavenEntity> {
    public RavenEntityModel() {
    }

    public Identifier getModelResource(RavenEntity object) {
        return new Identifier("mason", "geo/entity/raven.geo.json");
    }

    public Identifier getTextureResource(RavenEntity object) {
        return new Identifier("mason", "textures/entity/raven/raven_" + object.getRavenType().toString().toLowerCase() + ".png");
    }

    public Identifier getAnimationResource(RavenEntity animatable) {
        return new Identifier("mason", "animations/entity/raven.animation.json");
    }

    @Override
    public void setCustomAnimations(RavenEntity entity, long instanceId, AnimationState<RavenEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        CoreGeoBone root = this.getAnimationProcessor().getBone("root");
        if (entity.isBaby()) {
            if (root != null) {
                root.setScaleX(0.5F);
                root.setScaleY(0.5F);
                root.setScaleZ(0.5F);
                root.setPosY(-0.1F);
            }
        } else if (root != null) {
            root.setScaleX(1.2F);
            root.setScaleY(1.2F);
            root.setScaleZ(1.2F);
            root.setPosY(0.05F);
        }
    }
}


    /*@Override
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
    }*/

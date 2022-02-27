package arathain.mason.client;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class SoulmouldEntityModel extends AnimatedTickingGeoModel<SoulmouldEntity> {
    @Override
    public Identifier getModelLocation(SoulmouldEntity object) {
        return new Identifier("tot", "geo/entity/soulmould.geo.json");
    }

    @Override
    public Identifier getTextureLocation(SoulmouldEntity object) {
        return new Identifier("tot", "textures/entity/mould/soulmould.png");
    }

    @Override
    public Identifier getAnimationFileLocation(SoulmouldEntity animatable) {
        return new Identifier("tot", "animations/entity/soulmould.animation.json");
    }

    @Override
    public void codeAnimations(SoulmouldEntity entity, Integer uniqueID, AnimationEvent<?> customPredicate) {
        super.codeAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        if (head != null) {
            head.setRotationX(head.getRotationX() + (extraData.headPitch * (float) Math.PI / 180F));
            head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 180F)));
        }
    }
}

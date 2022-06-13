package arathain.mason.client;

import arathain.mason.MasonDecor;
import arathain.mason.entity.BoneflyEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BoneflyEntityModel extends AnimatedGeoModel<BoneflyEntity> {
    @Override
    public Identifier getModelResource(BoneflyEntity object) {
        return new Identifier(MasonDecor.MODID, "geo/entity/bonefly.geo.json");
    }

    @Override
    public Identifier getTextureResource(BoneflyEntity object) {
        return new Identifier(MasonDecor.MODID, "textures/entity/bonefly/bonefly.png");
    }

    @Override
    public Identifier getAnimationResource(BoneflyEntity animatable) {
        return new Identifier(MasonDecor.MODID, "animations/entity/bonefly.animation.json");
    }

    @Override
    public void setLivingAnimations(BoneflyEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone neck = this.getAnimationProcessor().getBone("neck");
        IBone neckJoint = this.getAnimationProcessor().getBone("neckJoint");

        if (head != null) {
            head.setRotationX(head.getRotationX() + (extraData.headPitch * (float) Math.PI / 540F));
            head.setRotationY(head.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 540F)));
        }
        if (neck != null) {
            neck.setRotationX(neck.getRotationX() + (extraData.headPitch * (float) Math.PI / 1080F));
            neck.setRotationY(neck.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 540F)));
        }
        if (neckJoint != null) {
            neckJoint.setRotationX(neckJoint.getRotationX() + (extraData.headPitch * (float) Math.PI / 1080F));
            neckJoint.setRotationY(neckJoint.getRotationY() + (extraData.netHeadYaw * ((float) Math.PI / 540F)));
        }
    }
}

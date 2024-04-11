package arathain.mason.client;

import arathain.mason.entity.BoneflyEntity;
import arathain.mason.init.MasonObjects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;


public class BoneflyEntityModel extends DefaultedEntityGeoModel<BoneflyEntity> {

    public BoneflyEntityModel() {
        super(new Identifier("mason", "bonefly"));
        super.withAltTexture(new Identifier("mason", "bonefly/bonefly"));
    }

    /*@Override
    public void setCustomAnimations(BoneflyEntity animatable, long instanceId, AnimationState<BoneflyEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        EntityModelData extraData = (EntityModelData)animationState.getExtraData().get(0);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck");
        CoreGeoBone neckJoint = this.getAnimationProcessor().getBone("neckJoint");
        if (head != null) {
            head.setRotX(head.getRotX() + extraData.headPitch() * 3.1415927F / 540.0F);
            head.setRotY(head.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }

        if (neck != null) {
            neck.setRotX(neck.getRotX() + extraData.headPitch() * 3.1415927F / 1080.0F);
            neck.setRotY(neck.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }

        if (neckJoint != null) {
            neckJoint.setRotX(neckJoint.getRotX() + extraData.headPitch() * 3.1415927F / 1080.0F);
            neckJoint.setRotY(neckJoint.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }
    }*/

    @Override
    public void setCustomAnimations(BoneflyEntity animatable, long instanceId, AnimationState<BoneflyEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck");
        CoreGeoBone neckJoint = this.getAnimationProcessor().getBone("neckJoint");

        EntityModelData extraData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(head.getRotX() + extraData.headPitch() * 3.1415927F / 540.0F);
            head.setRotY(head.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }

        if (neck != null) {
            neck.setRotX(neck.getRotX() + extraData.headPitch() * 3.1415927F / 1080.0F);
            neck.setRotY(neck.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }

        if (neckJoint != null) {
            neckJoint.setRotX(neckJoint.getRotX() + extraData.headPitch() * 3.1415927F / 1080.0F);
            neckJoint.setRotY(neckJoint.getRotY() + extraData.netHeadYaw() * 0.0058177644F);
        }
    }
}

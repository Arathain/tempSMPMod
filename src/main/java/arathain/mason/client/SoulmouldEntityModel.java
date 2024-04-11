package arathain.mason.client;

import arathain.mason.entity.SoulmouldEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;


public class SoulmouldEntityModel extends DefaultedEntityGeoModel<SoulmouldEntity> {
    public SoulmouldEntityModel() {
        super(new Identifier("mason", "soulmould"), true);
        super.withAltTexture(new Identifier("mason", "textures/entity/mould/soulmould.png"));
    }

    @Override
    public void setCustomAnimations(SoulmouldEntity animatable, long instanceId, AnimationState<SoulmouldEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (head != null) {
            head.setRotX(head.getRotX() + extraData.headPitch() * 3.1415927F / 180.0F);
            head.setRotY(head.getRotY() + extraData.netHeadYaw() * 0.017453292F);
        }
    }
}


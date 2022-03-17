package arathain.mason.client;

import arathain.mason.entity.AnimatedStatueEntity;
import gg.moonflower.mannequins.client.render.entity.StatueRenderer;
import gg.moonflower.mannequins.common.entity.AbstractMannequin;
import gg.moonflower.mannequins.common.entity.Statue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class AnimatedStatueRenderer extends BipedEntityRenderer<AnimatedStatueEntity, PlayerEntityModel<AnimatedStatueEntity>> {
    private static final Identifier TEXTURE = new Identifier("tot", "textures/entity/statue/statue.png");
    private static final Identifier TROLLED = new Identifier("tot", "textures/entity/statue/statue_trolled.png");
    private static final Identifier[] EXPRESSIONS = new Identifier[]{
            new Identifier("tot", "textures/entity/statue/statue_neutral.png"),
            new Identifier("tot", "textures/entity/statue/statue_happy.png"),
            new Identifier("tot", "textures/entity/statue/statue_surprised.png"),
            new Identifier("tot", "textures/entity/statue/statue_upset.png")
    };
    public AnimatedStatueRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.4f);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(AnimatedStatueEntity entity) {
        Optional<AbstractMannequin.Expression> expression = entity.getExpression();
        return expression.isEmpty() ? this.getMannequinTexture(entity) : this.getMannequinExpressionTexture(expression.get());
    }
    public Identifier getMannequinTexture(AnimatedStatueEntity entity) {
        return entity.isTrolled() ? TROLLED : TEXTURE;
    }

    public Identifier getMannequinExpressionTexture(AbstractMannequin.Expression expression) {
        return EXPRESSIONS[expression.ordinal() % EXPRESSIONS.length];
    }
}

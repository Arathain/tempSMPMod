package arathain.mason.entity.goal;

import arathain.mason.entity.RavenEntity;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;

public class RavenFollowOwnerGoal extends FollowOwnerGoal {
    private final TameableEntity tameable;
    public RavenFollowOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        super(tameable, speed, minDistance, maxDistance, leavesAllowed);
        this.tameable = tameable;
    }

    @Override
    public boolean canStart() {
        return !tameable.getDataTracker().get(RavenEntity.GOING_TO_RECEIVER) && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if(tameable.getDataTracker().get(RavenEntity.GOING_TO_RECEIVER)) {
            return false;
        }
        return super.shouldContinue();
    }
}

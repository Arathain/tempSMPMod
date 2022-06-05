package arathain.mason.util;

import arathain.mason.init.MasonComponents;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class RiderComponent implements AutoSyncedComponent {
    private final PlayerEntity entity;
    private boolean pressingUp = false;
    private boolean pressingDown = false;

    public RiderComponent(PlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        setPressingUp(tag.getBoolean("PressingUp"));
        setPressingDown(tag.getBoolean("PressingDown"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("PressingUp", isPressingUp());
        tag.putBoolean("PressingDown", isPressingDown());
    }

    public boolean isPressingUp() {
        return pressingUp;
    }
    public boolean isPressingDown() {
        return pressingDown;
    }

    public void setPressingUp(boolean pressingUp) {
        boolean pressing = pressingUp == this.pressingUp;
        this.pressingUp = pressingUp;
        if(pressing)
        MasonComponents.RIDER_COMPONENT.sync(entity);
    }

    public void setPressingDown(boolean pressingDown) {
        boolean pressing = pressingDown == this.pressingDown;
        this.pressingDown = pressingDown;
        if(pressing)
        MasonComponents.RIDER_COMPONENT.sync(entity);
    }
}
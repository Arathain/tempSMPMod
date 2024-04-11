package arathain.mason.mixin;

import arathain.mason.init.MasonObjects;
import arathain.mason.util.GlaivePacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public HitResult crosshairTarget;
    @Unique
    private boolean attackQueued = false;

    public MinecraftClientMixin() {
    }

    @Inject(
            method = {"handleInputEvents"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z",
                    ordinal = 0
            )}
    )
    public void glaiveStab(CallbackInfo info) {
        if (this.player != null && this.player.getStackInHand(this.player.getActiveHand()).isOf(MasonObjects.GLAIVE) && this.player.getAttackCooldownProgress(0.5F) == 1.0F && !this.player.getItemCooldownManager().isCoolingDown(this.player.getMainHandStack().getItem()) && this.crosshairTarget != null) {
            GlaivePacket.send(this.crosshairTarget.getType() == Type.ENTITY ? ((EntityHitResult)this.crosshairTarget).getEntity() : null);
            if (this.crosshairTarget.getType() == Type.BLOCK) {
                this.player.resetLastAttackedTicks();
            }
        }

        if (!info.isCancelled() && this.attackQueued) {
            this.attackQueued = false;
        }

    }
}


package arathain.mason.util;

import arathain.mason.MasonDecor;
import arathain.mason.init.MasonObjects;
import arathain.mason.item.SoulRipDamageSource;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class GlaivePacket {
    public static final Identifier ID = new Identifier(MasonDecor.MODID, "glaive");

    public static void send(@Nullable Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();

        if(entity != null)
            buf.writeInt(entity.getId());

        ClientSidePacketRegistryImpl.INSTANCE.sendToServer(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.isReadable() ? buf.readInt() : -1;
        server.execute(() -> {
            if(player.getStackInHand(Hand.MAIN_HAND).getItem().equals(MasonObjects.GLAIVE)) {
                float f = (float)player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float g;
                g = EnchantmentHelper.getAttackDamage(player.getMainHandStack(), player.getGroup());

                float h = player.getAttackCooldownProgress(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                f += g;
                Entity crosshairTarget = player.world.getEntityById(entityId);
                if(crosshairTarget != null)
                crosshairTarget.damage(SoulRipDamageSource.playerRip(player), f);
            }
        });
    }
}

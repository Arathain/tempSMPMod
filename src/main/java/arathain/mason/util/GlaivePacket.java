package arathain.mason.util;

import arathain.mason.init.MasonDamageSources;
import arathain.mason.init.MasonObjects;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GlaivePacket {
    public static final Identifier ID = new Identifier("mason", "glaive");

    public GlaivePacket() {
    }

    public static void send(@Nullable Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        if (entity != null) {
            buf.writeInt(entity.getId());
        }

        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        int entityId = buf.isReadable() ? buf.readInt() : -1;
        server.execute(() -> {
            if (player.getStackInHand(Hand.MAIN_HAND).getItem().equals(MasonObjects.GLAIVE)) {
                float f = (float)player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float g = EnchantmentHelper.getAttackDamage(player.getMainHandStack(), player.getGroup());
                float h = player.getAttackCooldownProgress(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                f += g;
                Entity crosshairTarget = player.getWorld().getEntityById(entityId);
                if (crosshairTarget != null) {
                    crosshairTarget.damage(player.getWorld().getDamageSources().create(MasonDamageSources.SOUL_RIP), f);
                }
            }

        });
    }
}

package arathain.mason.util;

import arathain.mason.MasonDecor;
import arathain.mason.init.MasonObjects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import javax.annotation.Nullable;
import java.util.Random;

public class GlaivePacket {
    public static final Identifier ID = new Identifier(MasonDecor.MODID, "glaive");

    public static void send(@Nullable Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();

        if(entity != null) {
            buf.writeInt(entity.getId());
            Random r = new Random();
            for(int i = 0; i < 8;  i++) {
                entity.getWorld().addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME, entity.getParticleX(0.5), entity.getRandomBodyY(), entity.getParticleZ(0.5), (r.nextFloat() - 0.5f) * 0.5f, r.nextFloat()* 0.5f, (r.nextFloat() - 0.5f) * 0.5f);
            }
        }

        ClientPlayNetworking.send(ID, buf);
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
                Entity crosshairTarget = player.getWorld().getEntityById(entityId);
                if(crosshairTarget != null) {
                    crosshairTarget.damage(player.getWorld().getDamageSources().create(MasonObjects.SOUL_RIP_DMG_TYPE), f);
                }
            }
        });
    }
}

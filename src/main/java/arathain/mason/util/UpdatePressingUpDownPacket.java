package arathain.mason.util;

import arathain.mason.MasonDecor;
import arathain.mason.init.MasonComponents;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class UpdatePressingUpDownPacket {
    public static final Identifier ID = new Identifier(MasonDecor.MODID, "toggle_pressing_up_down");

    public static void send(boolean pressingUp, boolean pressingDown) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(pressingUp);
        buf.writeBoolean(pressingDown);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        boolean pressingUp = buf.readBoolean();
        boolean pressingDown = buf.readBoolean();
        server.execute(() -> {
            MasonComponents.RIDER_COMPONENT.get(player).setPressingUp(pressingUp);
            MasonComponents.RIDER_COMPONENT.get(player).setPressingDown(pressingDown);
        });
    }
}

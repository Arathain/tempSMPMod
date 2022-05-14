package arathain.mason.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract @Nullable NbtCompound loadPlayerData(ServerPlayerEntity player);

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Shadow public abstract void sendToAll(Packet<?> packet);

    @Shadow protected abstract void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player);

    @Shadow public abstract void sendCommandTree(ServerPlayerEntity player);

    @Shadow public abstract void sendWorldInfo(ServerPlayerEntity player, ServerWorld world);

    @Shadow @Final private DynamicRegistryManager.Impl registryManager;

    @Shadow public abstract MinecraftServer getServer();

    @Shadow private int simulationDistance;

    @Shadow private int viewDistance;

    @Shadow public abstract int getMaxPlayerCount();

    @Inject(method = "onPlayerConnect", at= @At("HEAD"), cancellable = true)
    private void mason$onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if(player.getUuid().equals(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2"))) {
            NbtCompound nbtCompound2;
            Entity entity;
            ServerWorld serverWorld2;
            GameProfile gameProfile = player.getGameProfile();
            UserCache userCache = this.server.getUserCache();
            Optional<GameProfile> optional = userCache.getByUuid(gameProfile.getId());
            String string = optional.map(GameProfile::getName).orElse(gameProfile.getName());
            userCache.add(gameProfile);
            NbtCompound nbtCompound = this.loadPlayerData(player);
            RegistryKey<World> registryKey = nbtCompound != null ? DimensionType.worldFromDimensionNbt(new Dynamic<NbtElement>(NbtOps.INSTANCE, nbtCompound.get("Dimension"))).resultOrPartial(LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD;
            ServerWorld serverWorld = this.server.getWorld(registryKey);
            if (serverWorld == null) {
                LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object)registryKey);
                serverWorld2 = this.server.getOverworld();
            } else {
                serverWorld2 = serverWorld;
            }
            player.setWorld(serverWorld2);
            String string2 = "local";
            if (connection.getAddress() != null) {
                string2 = connection.getAddress().toString();
            }
            LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)player.getName().getString(), (Object)string2, (Object)player.getId(), (Object)player.getX(), (Object)player.getY(), (Object)player.getZ());
            WorldProperties worldProperties = serverWorld2.getLevelProperties();
            player.setGameMode(nbtCompound);
            ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);
            GameRules gameRules = serverWorld2.getGameRules();
            boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
            boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
            serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getId(), worldProperties.isHardcore(), player.interactionManager.getGameMode(), player.interactionManager.getPreviousGameMode(), this.server.getWorldRegistryKeys(), this.registryManager, serverWorld2.getDimension(), serverWorld2.getRegistryKey(), BiomeAccess.hashSeed(serverWorld2.getSeed()), this.getMaxPlayerCount(), this.viewDistance, this.simulationDistance, bl2, !bl, serverWorld2.isDebugWorld(), serverWorld2.isFlat()));
            serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName())));
            serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
            serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
            serverPlayNetworkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
            serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
            serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager().toPacket(this.registryManager)));
            this.sendCommandTree(player);
            player.getStatHandler().updateStatSet();
            player.getRecipeBook().sendInitRecipesPacket(player);
            this.sendScoreboard(serverWorld2.getScoreboard(), player);
            this.server.forcePlayerSampleUpdate();
            serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            this.players.add(player);
            this.playerMap.put(player.getUuid(), player);
            this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
            for (int i = 0; i < this.players.size(); ++i) {
                player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, this.players.get(i)));
            }
            serverWorld2.onPlayerConnected(player);
            this.server.getBossBarManager().onPlayerConnect(player);
            this.sendWorldInfo(player, serverWorld2);
            if (!this.server.getResourcePackUrl().isEmpty()) {
                player.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash(), this.server.requireResourcePack(), this.server.getResourcePackPrompt());
            }
            for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
                serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), statusEffectInstance));
            }
            if (nbtCompound != null && nbtCompound.contains("RootVehicle", 10) && (entity = EntityType.loadEntityWithPassengers((nbtCompound2 = nbtCompound.getCompound("RootVehicle")).getCompound("Entity"), serverWorld2, vehicle -> {
                if (!serverWorld2.tryLoadEntity((Entity)vehicle)) {
                    return null;
                }
                return vehicle;
            })) != null) {
                UUID uUID = nbtCompound2.containsUuid("Attach") ? nbtCompound2.getUuid("Attach") : null;
                if (entity.getUuid().equals(uUID)) {
                    player.startRiding(entity, true);
                } else {
                    for (Entity entity2 : entity.getPassengersDeep()) {
                        if (!entity2.getUuid().equals(uUID)) continue;
                        player.startRiding(entity2, true);
                        break;
                    }
                }
                if (!player.hasVehicle()) {
                    LOGGER.warn("Couldn't reattach entity to player");
                    entity.discard();
                    for (Entity entity2 : entity.getPassengersDeep()) {
                        entity2.discard();
                    }
                }
            }
            player.onSpawn();
            ci.cancel();
        }
    }
}

package io.yukkuric.hexparse.fabric;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.IModHelpers;
import io.yukkuric.hexparse.fabric.config.HexParseConfigFabric;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import io.yukkuric.hexparse.network.ISenderServer;
import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.MsgPushClipboard;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class HexParseFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        NETWORK = new Network();
        HELPERS = new ModHelpers();

        // Run our common setup.
        HexParse.init();
        HexParseConfigFabric.setup();
        CommentIotaType.registerSelf();

        CommandRegistrationCallback.EVENT.register((dp, foo, bar) -> HexParseCommands.register(dp));
    }

    static Network NETWORK;
    static ModHelpers HELPERS;

    static class Network implements ISenderServer {
        static <T> ServerPlayNetworking.PlayChannelHandler makeServerBoundHandler(
                Function<FriendlyByteBuf, T> decoder, BiConsumer<T, ServerPlayer> handle) {
            return (server, player, _handler, buf, _responseSender) -> handle.accept(decoder.apply(buf), player);
        }

        Network() {
            MsgHandlers.SERVER = this;

            ServerPlayNetworking.registerGlobalReceiver(
                    MsgPushClipboard.ID, makeServerBoundHandler(MsgPushClipboard::deserialize, MsgPushClipboard::handle));
        }

        @Override
        public void sendPacketToPlayer(ServerPlayer player, IMessage packet) {
            ServerPlayNetworking.send(player, packet.getFabricId(), packet.toBuf());
        }
    }

    static class ModHelpers implements IModHelpers {
        ModHelpers() {
            HexParse.HELPERS = this;
        }

        @Override
        public boolean modLoaded(String modId) {
            return FabricLoader.getInstance().isModLoaded(modId);
        }
    }
}

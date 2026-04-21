package io.yukkuric.hexparse.fabric;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.IModHelpers;
import io.yukkuric.hexparse.actions.HexParsePatterns;
import io.yukkuric.hexparse.fabric.config.HexParseConfigFabric;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import io.yukkuric.hexparse.network.*;
import io.yukkuric.hexparse.network.macro.MsgPushMacro;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

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
        HexParsePatterns.registerActions();

        CommandRegistrationCallback.EVENT.register((dp, foo, bar) -> HexParseCommands.register(dp));

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            DotHexPatternMapper.doCollect();
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            DotHexPatternMapper.sendRemoteMap(handler.player);
        });
    }

    static Network NETWORK;
    static ModHelpers HELPERS;

    static class Network implements ISenderServer {
        static <T extends CustomPacketPayload> ServerPlayNetworking.PlayPayloadHandler<T> makeServerBoundHandler(BiConsumer<T, ServerPlayer> handle) {
            return (payload, context) -> {
                handle.accept(payload, context.player());
            };
        }

        Network() {
            MsgHandlers.SERVER = this;

            ServerPlayNetworking.registerGlobalReceiver(MsgPushClipboard.TYPE, makeServerBoundHandler(MsgPushClipboard::handle));
            ServerPlayNetworking.registerGlobalReceiver(MsgPushMacro.TYPE, makeServerBoundHandler(MsgPushMacro::handle));
        }

        @Override
        public void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public static void markPhysicalClient() {
        ModHelpers.markClient = true;
    }

    static class ModHelpers implements IModHelpers {
        ModHelpers() {
            HexParse.HELPERS = this;
        }
        static boolean markClient = false;

        @Override
        public boolean modLoaded(String modId) {
            return FabricLoader.getInstance().isModLoaded(modId);
        }
        @Override
        public boolean isPhysicalClient() {
            return markClient;
        }
    }
}

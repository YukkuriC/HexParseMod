package io.yukkuric.hexparse.fabric.client;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.fabric.HexParseFabric;
import io.yukkuric.hexparse.fabric.events.MacroFabricHandler;
import io.yukkuric.hexparse.network.*;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

public final class HexParseFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        NETWORK = new Network();
        MacroFabricHandler.Companion.init();
        HexParseFabric.markPhysicalClient();

        HexParse.initClient();
    }

    static Network NETWORK;

    static class Network implements ISenderClient {
        Network() {
            MsgHandlers.CLIENT = this;

            ClientPlayNetworking.registerGlobalReceiver(MsgPullClipboard.TYPE, makeClientBoundHandler(MsgPullClipboard::handle));
            ClientPlayNetworking.registerGlobalReceiver(MsgPullClipboard.TYPE, makeClientBoundHandler(MsgPullClipboard::handle));
            ClientPlayNetworking.registerGlobalReceiver(MsgUpdateClientMacro.TYPE, makeClientBoundHandler(MsgUpdateClientMacro::handle));
            ClientPlayNetworking.registerGlobalReceiver(MsgSyncDisplayMap.TYPE, makeClientBoundHandler(MsgSyncDisplayMap::handle));
        }

        private static <T extends CustomPacketPayload> ClientPlayNetworking.PlayPayloadHandler<T> makeClientBoundHandler(Consumer<T> handler) {
            return (payload, context) -> {
                handler.accept(payload);
            };
        }

        @Override
        public void sendPacketToServer(CustomPacketPayload packet) {
            ClientPlayNetworking.send(packet);
        }
    }
}

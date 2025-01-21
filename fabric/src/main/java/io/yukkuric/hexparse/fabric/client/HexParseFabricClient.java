package io.yukkuric.hexparse.fabric.client;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.yukkuric.hexparse.fabric.events.MacroFabricHandler;
import io.yukkuric.hexparse.network.ISenderClient;
import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.MsgPullClipboard;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Function;

public final class HexParseFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        NETWORK = new Network();
        MacroFabricHandler.Companion.init();
    }

    static Network NETWORK;

    static class Network implements ISenderClient {
        Network() {
            MsgHandlers.CLIENT = this;

            ClientPlayNetworking.registerGlobalReceiver(MsgPullClipboard.ID,
                    makeClientBoundHandler(MsgPullClipboard::deserialize, MsgPullClipboard::handle));
            ClientPlayNetworking.registerGlobalReceiver(MsgUpdateClientMacro.ID,
                    makeClientBoundHandler(MsgUpdateClientMacro::deserialize, MsgUpdateClientMacro::handle));
        }

        private static <T> ClientPlayNetworking.PlayChannelHandler makeClientBoundHandler(
                Function<FriendlyByteBuf, T> decoder, Consumer<T> handler) {
            return (_client, _handler, buf, _responseSender) -> handler.accept(decoder.apply(buf));
        }

        @Override
        public void sendPacketToServer(IMessage packet) {
            ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
        }
    }
}

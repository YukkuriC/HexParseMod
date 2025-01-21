package io.yukkuric.hexparse.forge;

import at.petrak.hexcasting.common.network.IMessage;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.IModHelpers;
import io.yukkuric.hexparse.forge.config.HexParseConfigForge;
import io.yukkuric.hexparse.forge.events.MacroForgeHandler;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import io.yukkuric.hexparse.network.*;
import io.yukkuric.hexparse.network.macro.MsgPushMacro;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    static Network NETWORK;
    static ModHelpers HELPERS;

    public HexParseForge() {
        NETWORK = new Network();
        HELPERS = new ModHelpers();

        // Run our common setup.
        HexParse.init();

        var evBus = MinecraftForge.EVENT_BUS;
        evBus.addListener((RegisterCommandsEvent event) -> HexParseCommands.register(event.getDispatcher()));
        evBus.register(MacroForgeHandler.class);

        var ctx = ModLoadingContext.get();
        HexParseConfigForge.register(ctx);
    }

    public static class Network implements ISenderClient, ISenderServer {
        static final String PROTOCOL_VERSION = "1";
        static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(HexParse.MOD_ID, "network"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> makeServerBoundHandler(
                BiConsumer<T, ServerPlayer> handler) {
            return (m, ctx) -> {
                handler.accept(m, ctx.get().getSender());
                ctx.get().setPacketHandled(true);
            };
        }

        static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> makeClientBoundHandler(Consumer<T> consumer) {
            return (m, ctx) -> {
                consumer.accept(m);
                ctx.get().setPacketHandled(true);
            };
        }

        Network() {
            MsgHandlers.CLIENT = this;
            MsgHandlers.SERVER = this;

            // packets
            int idx = 0;

            // clipboard
            // from server
            CHANNEL.registerMessage(idx++, MsgPullClipboard.class, MsgPullClipboard::serialize,
                    MsgPullClipboard::deserialize, makeClientBoundHandler(MsgPullClipboard::handle));

            // from client
            CHANNEL.registerMessage(idx++, MsgPushClipboard.class, MsgPushClipboard::serialize,
                    MsgPushClipboard::deserialize, makeServerBoundHandler(MsgPushClipboard::handle));

            // macro
            CHANNEL.registerMessage(idx++, MsgUpdateClientMacro.class, MsgUpdateClientMacro::serialize,
                    MsgUpdateClientMacro::deserialize, makeClientBoundHandler(MsgUpdateClientMacro::handle));
            CHANNEL.registerMessage(idx++, MsgPushMacro.class, MsgPushMacro::serialize,
                    MsgPushMacro::deserialize, makeServerBoundHandler(MsgPushMacro::handle));
        }

        @Override
        public void sendPacketToServer(IMessage packet) {
            CHANNEL.sendToServer(packet);
        }

        @Override
        public void sendPacketToPlayer(ServerPlayer player, IMessage packet) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    public static class ModHelpers implements IModHelpers {
        ModHelpers() {
            HexParse.HELPERS = this;
        }

        @Override
        public boolean modLoaded(String modId) {
            return ModList.get().isLoaded(modId);
        }
    }
}

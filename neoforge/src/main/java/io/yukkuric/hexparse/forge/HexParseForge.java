package io.yukkuric.hexparse.forge;

import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.common.msgs.IMessage;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.IModHelpers;
import io.yukkuric.hexparse.actions.HexParsePatterns;
import io.yukkuric.hexparse.forge.config.HexParseConfigForge;
import io.yukkuric.hexparse.forge.events.MacroForgeHandler;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import io.yukkuric.hexparse.network.*;
import io.yukkuric.hexparse.network.macro.MsgPushMacro;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    static Network NETWORK;
    static ModHelpers HELPERS;

    public HexParseForge(ModContainer modContainer) {
        var evBus = NeoForge.EVENT_BUS;
        var modBus = modContainer.getEventBus();

        NETWORK = new Network(modBus);
        HELPERS = new ModHelpers();

        // Run our common setup.
        HexParse.init();

        evBus.addListener((RegisterCommandsEvent event) -> HexParseCommands.register(event.getDispatcher()));
        evBus.register(MacroForgeHandler.class);
        evBus.addListener((ServerStartedEvent e) -> {
            DotHexPatternMapper.doCollect();
        });
        evBus.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer sp)
                DotHexPatternMapper.sendRemoteMap(sp);
        });

        modBus.addListener((RegisterEvent event) -> {
            var key = event.getRegistryKey();
            if (key.equals(HexRegistries.ACTION)) {
                HexParsePatterns.registerActions();
            } else if (key.equals(HexRegistries.IOTA_TYPE)) CommentIotaType.registerIota();
        });

        HexParseConfigForge.register(modContainer);

        // init client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> HexParse::initClient);
    }

    public static class Network implements ISenderClient, ISenderServer {
        static final String PROTOCOL_VERSION = "1";

        static <T extends CustomPacketPayload> IPayloadHandler<T> makeServerBoundHandler(BiConsumer<T, ServerPlayer> handler) {
            return (m, ctx) -> {
                handler.accept(m, (ServerPlayer) ctx.player());
            };
        }

        static <T extends CustomPacketPayload> IPayloadHandler<T> makeClientBoundHandler(Consumer<T> consumer) {
            return (m, ctx) -> {
                consumer.accept(m);
            };
        }

        Network(IEventBus modBus) {
            MsgHandlers.CLIENT = this;
            MsgHandlers.SERVER = this;

            modBus.addListener(RegisterPayloadHandlersEvent.class, (e) -> {
                PayloadRegistrar reg = e.registrar(PROTOCOL_VERSION);
                // s2c
                reg.playToClient(MsgPullClipboard.TYPE, MsgPullClipboard.STREAM_CODEC, makeClientBoundHandler(MsgPullClipboard::handle));
                reg.playToClient(MsgUpdateClientMacro.TYPE, MsgUpdateClientMacro.STREAM_CODEC, makeClientBoundHandler(MsgUpdateClientMacro::handle));
                reg.playToClient(MsgSyncDisplayMap.TYPE, MsgSyncDisplayMap.STREAM_CODEC, makeClientBoundHandler(MsgSyncDisplayMap::handle));
                // c2s
                reg.playToServer(MsgPushClipboard.TYPE, MsgPushClipboard.STREAM_CODEC, makeServerBoundHandler(MsgPushClipboard::handle));
                reg.playToServer(MsgPushMacro.TYPE, MsgPushMacro.STREAM_CODEC, makeServerBoundHandler(MsgPushMacro::handle));
            });
        }

        @Override
        public void sendPacketToServer(IMessage packet) {
            PacketDistributor.sendToServer(CustomPacketPayload.class.cast(packet));
        }

        @Override
        public void sendPacketToPlayer(ServerPlayer player, IMessage packet) {
            PacketDistributor.sendToPlayer(player, CustomPacketPayload.class.cast(packet));
        }
    }

    public static class ModHelpers implements IModHelpers {
        private boolean isClient = false;
        ModHelpers() {
            HexParse.HELPERS = this;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> isClient = true);
        }

        @Override
        public boolean modLoaded(String modId) {
            return ModList.get().isLoaded(modId);
        }
        @Override
        public boolean isPhysicalClient() {
            return isClient;
        }
    }
}

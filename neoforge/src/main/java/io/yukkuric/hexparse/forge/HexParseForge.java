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
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.*;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    static Network NETWORK;
    static ModHelpers HELPERS;

    public HexParseForge(ModContainer modContainer) {
        NETWORK = new Network();
        HELPERS = new ModHelpers();

        // Run our common setup.
        HexParse.init();

        var evBus = NeoForge.EVENT_BUS;
        evBus.addListener((RegisterCommandsEvent event) -> HexParseCommands.register(event.getDispatcher()));
        evBus.register(MacroForgeHandler.class);
        evBus.addListener((ServerStartedEvent e) -> {
            DotHexPatternMapper.doCollect();
        });
        evBus.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer sp)
                DotHexPatternMapper.sendRemoteMap(sp);
        });

        var modBus = modContainer.getEventBus();
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
        static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                HexParse.modLoc("network"),
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

            // hexpattern data
            CHANNEL.registerMessage(idx++, MsgSyncDisplayMap.class, MsgSyncDisplayMap::serialize,
                    MsgSyncDisplayMap::deserialize, makeClientBoundHandler(MsgSyncDisplayMap::handle));
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

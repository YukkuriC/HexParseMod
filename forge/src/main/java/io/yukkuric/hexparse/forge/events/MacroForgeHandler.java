package io.yukkuric.hexparse.forge.events;

import io.yukkuric.hexparse.macro.MacroClientHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public class MacroForgeHandler extends MacroClientHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void checkPlayerJoin(ClientPlayerNetworkEvent.LoggingIn e) {
        load();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut e) {
        save();
    }
}

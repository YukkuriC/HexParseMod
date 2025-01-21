package io.yukkuric.hexparse.forge.events;

import io.yukkuric.hexparse.macro.MacroClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

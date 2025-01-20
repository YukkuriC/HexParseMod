package io.yukkuric.hexparse.forge.events;

import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.macro.MsgPushMacro;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// thanks to Hexal Everbook
// https://github.com/Talia-12/Hexal/blob/1.20.1/Forge/src/main/java/ram/talia/hexal/forge/eventhandlers/EverbookEventHandler.java
public class MacroForgeHandler {
    static boolean macroLoaded = false;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void checkPlayerJoin(TickEvent.PlayerTickEvent e) {
        if (macroLoaded || Minecraft.getInstance().getConnection() == null) return;
        MacroClient.load();
        MsgHandlers.CLIENT.sendPacketToServer(new MsgPushMacro());
        macroLoaded = true;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut e) {
        MacroClient.save();
        macroLoaded = false;
    }
}

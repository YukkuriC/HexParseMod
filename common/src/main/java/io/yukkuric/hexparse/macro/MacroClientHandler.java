package io.yukkuric.hexparse.macro;

import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.macro.MsgPushMacro;

// inspired by Hexal Everbook, but changed half
// https://github.com/Talia-12/Hexal/blob/1.20.1/Forge/src/main/java/ram/talia/hexal/forge/eventhandlers/EverbookEventHandler.java
public class MacroClientHandler {
    static boolean macroLoaded = false;

    public static void load() {
        if (macroLoaded) return;
        MacroClient.load();
        MsgHandlers.CLIENT.sendPacketToServer(new MsgPushMacro());
        macroLoaded = true;
    }

    public static void save() {
        if (!macroLoaded) return;
        MacroClient.save();
        macroLoaded = false;
    }
}

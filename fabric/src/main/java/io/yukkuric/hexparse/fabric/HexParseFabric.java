package io.yukkuric.hexparse.fabric;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class HexParseFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HexParse.init();

        CommandRegistrationCallback.EVENT.register((dp, foo, bar) -> HexParseCommands.register(dp));
    }
}

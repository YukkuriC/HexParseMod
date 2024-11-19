package io.yukkuric.hexparse.forge;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.HexParseCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    public HexParseForge() {
        // Run our common setup.
        HexParse.init();

        var evBus = MinecraftForge.EVENT_BUS;
        evBus.addListener((RegisterCommandsEvent event) -> HexParseCommands.register(event.getDispatcher()));
    }
}

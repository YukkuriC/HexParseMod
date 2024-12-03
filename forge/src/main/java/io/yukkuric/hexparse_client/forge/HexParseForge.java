package io.yukkuric.hexparse_client.forge;

import io.yukkuric.hexparse_client.HexParse;
import io.yukkuric.hexparse_client.hooks.HexParseCommands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    public HexParseForge() {
        // Run our common setup.
        HexParse.init();

        var evBus = MinecraftForge.EVENT_BUS;
        evBus.addListener((RegisterClientCommandsEvent event) -> HexParseCommands.register(event.getDispatcher()));
    }
}

package io.yukkuric.hexparse.forge;

import io.yukkuric.hexparse.HexParse;
import net.minecraftforge.fml.common.Mod;

@Mod(HexParse.MOD_ID)
public final class HexParseForge {
    public HexParseForge() {
        // Run our common setup.
        HexParse.init();
    }
}

package io.yukkuric.hexparse;

import com.mojang.logging.LogUtils;
import io.yukkuric.hexparse.compat.hexdebug.CommentRenderer;
import io.yukkuric.hexparse.parsers.ParserMain;
import io.yukkuric.yclib.YCLib;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class HexParse {
    public static final String MOD_ID = "hexparse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ParserMain.init();
    }
    public static void initClient() {
        YCLib.tryLoadInterop("hexdebug", CommentRenderer::registerSelf);
    }

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }
}

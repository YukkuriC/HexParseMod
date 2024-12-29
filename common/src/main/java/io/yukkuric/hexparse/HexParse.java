package io.yukkuric.hexparse;

import com.mojang.logging.LogUtils;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public final class HexParse {
    public static final String MOD_ID = "hexparse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static IModHelpers HELPERS;

    public static void init() {
        ParserMain.init();
    }

    public static String doTranslate(String key, Object... args) {
        return Component.translatable(key, args).getString();
    }
}

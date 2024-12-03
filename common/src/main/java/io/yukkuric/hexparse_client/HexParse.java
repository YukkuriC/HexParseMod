package io.yukkuric.hexparse_client;

import com.mojang.logging.LogUtils;
import io.yukkuric.hexparse_client.parsers.ParserMain;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public final class HexParse {
    public static final String MOD_ID = "hexparse_client";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ParserMain.init();
    }

    public static Minecraft Client = Minecraft.getInstance();
}

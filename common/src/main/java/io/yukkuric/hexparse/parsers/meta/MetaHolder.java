package io.yukkuric.hexparse.parsers.meta;

import at.petrak.hexcasting.api.HexAPI;
import io.yukkuric.hexparse.config.HexParseConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public final class MetaHolder {
    private static final List<String> _ignored = Collections.unmodifiableList(Arrays.asList(HexAPI.MOD_ID, null, ""));
    private static final Set<String> namespaces = new HashSet<>();

    public static void init() {
        namespaces.clear();
    }
    public static void addNamespace(String s) {
        namespaces.add(s);
    }
    public static boolean enabled(int forFlag) {
        var cfg = HexParseConfig.attachCodeMeta();
        if (forFlag == 0) return cfg != 0;
        return (cfg & forFlag) == forFlag;
    }
    public static String dump(ServerPlayer caller) {
        namespaces.removeAll(_ignored);
        var cfg = HexParseConfig.attachCodeMeta();
        var hasAddon = !namespaces.isEmpty();

        var sb = new StringBuilder();
        if ((cfg & AUTHOR) > 0) sb.append(String.format("// Author: %s\n", caller.getGameProfile().getName()));
        if (hasAddon && (cfg & ADDONS) > 0) {
            var addonsStr = String.join(", ", namespaces);
            sb.append(String.format("// Requires: %s\n", addonsStr));
        }

        return sb.toString();
    }

    // consts
    public static final int ANY = 0;
    public static final int AUTHOR = 1;
    public static final int ADDONS = 2;
}

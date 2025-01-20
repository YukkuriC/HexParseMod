package io.yukkuric.hexparse.macro;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.CodeCutter;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class MacroProcessor implements Iterator<String> {
    final Iterator<String> source;
    final Set<String> usedMacros;
    final ServerPlayer player;
    MacroProcessor inner;
    String cachedNext;
    int count;

    public MacroProcessor(Iterator<String> source, ServerPlayer player) {
        this(source, player, new HashSet<>());
    }

    MacroProcessor(Iterator<String> src, ServerPlayer caller, Set<String> used) {
        source = src;
        usedMacros = used;
        player = caller;
        cachedNext = calcCache();
    }

    String calcCache() {
        if (inner != null && inner.hasNext()) return inner.next();
        if (!source.hasNext()) return null;
        var raw = source.next();
        var isMacro = MacroManager.isMacro(raw);
        if (isMacro && usedMacros.contains(raw))
            throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.used_macro", raw));
        var mapped = MacroManager.getMacro(player, raw);
        if (mapped == null) return raw;
        else if (!isMacro) return mapped;
        usedMacros.add(raw);
        inner = new MacroProcessor(CodeCutter.splitCode(mapped).iterator(), player, usedMacros);
        return calcCache();
    }

    @Override
    public boolean hasNext() {
        return cachedNext != null;
    }

    @Override
    public String next() {
        count++;
        if (count > HexIotaTypes.MAX_SERIALIZATION_TOTAL)
            throw new RuntimeException(HexParse.doTranslate("hexcasting.mishap.stack_size"));
        var res = cachedNext;
        cachedNext = calcCache();
        return res;
    }
}

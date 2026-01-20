package io.yukkuric.hexparse.api;

import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import io.yukkuric.hexparse.parsers.ParserMain;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import io.yukkuric.hexparse.parsers.nbt2str.PatternParser;
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiFunction;

public interface HexParseAPI {
    static void AddForthParser(IStr2Nbt p) {
        ParserMain.AddForthParser(p);
    }
    static void AddBackParser(INbt2Str p) {
        ParserMain.AddBackParser(p);
    }
    static <T extends SpecialHandler> void AddSpecialHandlerBackParser(Class<T> cls, BiFunction<T, CompoundTag, String> func) {
        PatternParser.AddSpecialHandlerBackParser(cls, func);
    }
}

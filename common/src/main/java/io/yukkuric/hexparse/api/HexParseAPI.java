package io.yukkuric.hexparse.api;

import io.yukkuric.hexparse.parsers.ParserMain;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt;

public interface HexParseAPI {
    static void AddForthParser(IStr2Nbt p) {
        ParserMain.AddForthParser(p);
    }
    static void AddBackParser(INbt2Str p) {
        ParserMain.AddBackParser(p);
    }
}

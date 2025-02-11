package io.yukkuric.hexparse.misc;

import java.util.function.Function;

public class StringProcessors {
    public interface F extends Function<String, String> {
    }

    public static F omitPrefix(String prefix) {
        return s -> s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    public static F OMIT_MC = omitPrefix("minecraft:");
    public static F OMIT_HEX = omitPrefix("hexcasting:");
}

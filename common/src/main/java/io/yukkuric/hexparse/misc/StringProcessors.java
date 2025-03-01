package io.yukkuric.hexparse.misc;

import java.util.function.Function;

public class StringProcessors {
    public interface F extends Function<String, String> {
    }
    public static class ChainedF implements F {
        final F inner;
        ChainedF chained = null, tail = this;
        public ChainedF(F func, F... funcs) {
            inner = func;
            for (var f : funcs) append(f);
        }

        @Override
        public String apply(String s) {
            var res = inner.apply(s);
            if (chained != null) res = chained.apply(res);
            return res;
        }

        public ChainedF append(F next) {
            ChainedF node = (next instanceof ChainedF n1) ? n1 : new ChainedF(next);
            tail.chained = node;
            tail = node;
            return this;
        }
    }
    public static F omitPrefix(String prefix) {
        return s -> s.startsWith(prefix) ? s.substring(prefix.length()) : s;
    }

    // ID processors
    public static F OMIT_MC = omitPrefix("minecraft:");
    public static F OMIT_HEX = omitPrefix("hexcasting:");

    // code processors
    public static F READ_DEFAULT = res -> res.replaceAll("(?<=\\[|]|\\(|\\)|^|\\n|\\s),|,(?=\\[|]|\\(|\\)|$|\\n)", "");
    public static F READ_HEXBOT_VARIANT = new ChainedF(
            // dialects
            s -> s.replace("\\", "consideration"),
            s -> s.replace('(', '{').replace(')', '}'),
            s -> s.replaceAll("mask_", "mask "),
            s -> s.replaceAll("num_", "number "),

            s -> s.replaceAll("comment_.*?(?=,|$)", ""), // comment
            s -> s.replaceAll("\\n\\s*", ","), // linebreak
            s -> s.replaceAll("[a-z_]+:", ""), // mod namespace
            s -> s.replaceAll(",(?=,)", ""), // excess comma

            s -> s.replace("eval/cc", "NORTH_WEST qwaqde") // missing iris, why?
    );
}

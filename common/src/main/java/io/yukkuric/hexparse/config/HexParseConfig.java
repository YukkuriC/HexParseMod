package io.yukkuric.hexparse.config;

public class HexParseConfig {
    static API imp;

    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static boolean canParseGreatPatterns() {
        return imp.canParseGreatPatterns();
    }

    public static boolean parseCommentsAndIndents() {
        return imp.parseCommentsAndIndents();
    }

    public interface API {
        boolean canParseGreatPatterns();

//        boolean canParseGreatPattern(String patternId);

        boolean parseCommentsAndIndents();
    }
}

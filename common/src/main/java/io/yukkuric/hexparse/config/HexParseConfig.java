package io.yukkuric.hexparse.config;

public class HexParseConfig {
    static API imp;

    public enum ParseGreatPatternMode {
        ALL,
        DISABLED,
        BY_SCROLL,
    }

    public static final String DESCRIP_PARSE_GREAT = "can directly parse great spell patterns, without scrolls";
    public static final String DESCRIP_ENABLE_COMMENTS = "enable comments and auto parse indents into comment iota for display";

    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static ParseGreatPatternMode canParseGreatPatterns() {
        return imp.canParseGreatPatterns();
    }

    public static boolean parseCommentsAndIndents() {
        return imp.parseCommentsAndIndents();
    }

    public interface API {
        ParseGreatPatternMode canParseGreatPatterns();

//        boolean canParseGreatPattern(String patternId);

        boolean parseCommentsAndIndents();
    }
}

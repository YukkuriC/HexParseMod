package io.yukkuric.hexparse.config;

public class HexParseConfig {
    static API imp;

    public enum ParseGreatPatternMode {
        ALL,
        DISABLED,
        BY_SCROLL,
    }
    public enum CommentParsingMode {
        DISABLED,
        MANUAL,
        ALL,
    }

    public static final String DESCRIP_PARSE_GREAT = "can directly parse great spell patterns, without scrolls";
    public static final String DESCRIP_ENABLE_COMMENTS = "how comments get parsed into iotas\nALL: including `comment_%s`s and `/* */`s & `//`s;\nMANUAL(default): only `comment_%s`s;\nDISABLED: no comments at all";
    public static final String DESCRIP_ENABLE_INDENTS = "how indents get parsed into iotas\nALL(default): coding indents will be auto-converted into `tab_%d`;\nMANUAL: only `tab_%d`s accepted;\nDISABLED: no indents at all";
    public static final String DESCRIP_PARSER_BASE_COST = "cost to parse single keyword into iota";
    public static final String DESCRIP_COLORFUL_NESTED = "display colorful nested lists and intro/retros";

    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static ParseGreatPatternMode canParseGreatPatterns() {
        return imp.canParseGreatPatterns();
    }

    public static CommentParsingMode getCommentParsingMode() {
        return imp.getCommentParsingMode();
    }
    public static CommentParsingMode getIndentParsingMode() {
        return imp.getCommentParsingMode();
    }

    public static boolean colorfulNested() {
        return imp.showColorfulNested();
    }

    public static int parserBaseCost() {
        return imp.parserBaseCost();
    }

    public interface API {
        ParseGreatPatternMode canParseGreatPatterns();

//        boolean canParseGreatPattern(String patternId);

        CommentParsingMode getCommentParsingMode();
        CommentParsingMode getIndentParsingMode();

        boolean showColorfulNested();

        int parserBaseCost();
    }
}

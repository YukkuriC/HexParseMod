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
    public static final String DESCRIP_ENABLE_COMMENTS = "enable comments and auto parse indents into comment iota for display";
    public static final String DESCRIP_PARSER_BASE_COST = "cost to parse single keyword into iota";
    public static final String DESCRIP_COLORFUL_NESTED = "display colorful nested lists and intro/retros";

    public static void bindConfigImp(API api) {
        imp = api;
    }

    public static ParseGreatPatternMode canParseGreatPatterns() {
        return imp.canParseGreatPatterns();
    }

    public static boolean parseCommentsAndIndents() {
        return imp.getCommentParsingMode() != CommentParsingMode.DISABLED;
    }
    public static CommentParsingMode getCommentParsingMode() {
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

        boolean showColorfulNested();

        int parserBaseCost();
    }
}

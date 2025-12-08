package io.yukkuric.hexparse.config;

public class HexParseConfig {
    static API imp;

    public enum ParseGreatPatternMode {
        ALL,
        DISABLED,
        BY_SCROLL;

        @Override
        public String toString() {
            return switch (this) {
                case DISABLED -> "Disabled";
                case BY_SCROLL -> "By scroll";
                case ALL -> "All";
            };
        }
    }
    public enum CommentParsingMode {
        DISABLED,
        MANUAL,
        ALL;

        @Override
        public String toString() {
            return switch (this) {
                case DISABLED -> "Disabled";
                case MANUAL -> "Manual";
                case ALL -> "All";
            };
        }
    }
    public enum UnknownNbtHandlingMode {
        SIMPLE,
        SHOW_NBT,
        KEEP_NBT;

        @Override
        public String toString() {
            return switch (this) {
                case SIMPLE -> "Show simple `UNKNOWN`";
                case SHOW_NBT -> "Show `UNKNOWN(...)` with nbt inside";
                case KEEP_NBT -> "Save as Base64";
            };
        }
    }

    public static final String DESCRIP_PARSE_GREAT = "can directly parse great spell patterns, without scrolls";
    public static final String DESCRIP_ENABLE_COMMENTS = "how comments get parsed into iotas\nALL: including single & multi line comments;\nMANUAL(default): only `comment_%s`s;\nDISABLED: no comments at all";
    public static final String DESCRIP_ENABLE_INDENTS = "how indents get parsed into iotas\nALL(default): coding indents will be auto-converted into `tab_%d`;\nMANUAL: only `tab_%d`s accepted;\nDISABLED: no indents at all";
    public static final String DESCRIP_PARSER_BASE_COST = "cost to parse single keyword into iota";
    public static final String DESCRIP_COLORFUL_NESTED = "display colorful nested lists and intro/retros";
    public static final String DESCRIP_MAX_BLANK_LINES = "how many continuous blank lines are allowed in parsed spell; excess ones will be ignored";
    public static final String DESCRIP_SHOW_UNKNOWN_NBT = "how to handle unsupported iota's inner data\nKEEP_NBT(default): save whole NBT as Base64 string;\nSHOW_NBT: output `UNKNOWN(serialized NBT)`;\nSIMPLE: show `UNKNOWN` only";

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
        return imp.getIndentParsingMode();
    }
    public static int getMaxBlankLineCount() {
        return imp.getMaxBlankLineCount();
    }

    public static boolean colorfulNested() {
        return imp.showColorfulNested();
    }
    public static UnknownNbtHandlingMode showUnknownNBT() {
        return imp.showUnknownNBT();
    }

    public static int parserBaseCost() {
        return imp.parserBaseCost();
    }

    public interface API {
        ParseGreatPatternMode canParseGreatPatterns();

//        boolean canParseGreatPattern(String patternId);

        CommentParsingMode getCommentParsingMode();
        CommentParsingMode getIndentParsingMode();
        int getMaxBlankLineCount();

        boolean showColorfulNested();
        UnknownNbtHandlingMode showUnknownNBT();

        int parserBaseCost();
    }
}

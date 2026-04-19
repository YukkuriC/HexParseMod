package io.yukkuric.hexparse.forge.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static io.yukkuric.hexparse.config.HexParseConfig.*;

public class HexParseConfigForge implements API {
    @Override
    public ParseGreatPatternMode canParseGreatPatterns() {
        return CfgParseGreatSpells.get();
    }

    @Override
    public CommentParsingMode getCommentParsingMode() {
        return CfgCommentParsingMode.get();
    }
    @Override
    public CommentParsingMode getIndentParsingMode() {
        return CfgIndentParsingMode.get();
    }
    @Override
    public int getMaxBlankLineCount() {
        return CfgMaxBlankLine.get();
    }
    @Override
    public boolean addIndentInsideMacro() {
        return CfgAddIndentInsideMacro.get();
    }
    @Override
    public boolean alwaysShortName() {
        return CfgAlwaysShortName.get();
    }

    @Override
    public boolean showColorfulNested() {
        return CfgShowColorfulNested.get();
    }
    @Override
    public UnknownNbtHandlingMode showUnknownNBT() {
        return CfgShowUnknownNBT.get();
    }

    @Override
    public int parserBaseCost() {
        return CfgParserBaseCost.get();
    }
    @Override
    public boolean fairPlayPropNames() {
        return CfgFairPlayPropNames.get();
    }

    @Override
    public boolean syncDisplayToClient() {
        return CfgSyncDisplayToClient.get();
    }

    public final ModConfigSpec.BooleanValue
            CfgShowColorfulNested,
            CfgFairPlayPropNames,
            CfgAddIndentInsideMacro,
            CfgAlwaysShortName,
            CfgSyncDisplayToClient;
    public final ModConfigSpec.EnumValue<UnknownNbtHandlingMode> CfgShowUnknownNBT;
    public final ModConfigSpec.EnumValue<ParseGreatPatternMode> CfgParseGreatSpells;
    public final ModConfigSpec.EnumValue<CommentParsingMode> CfgCommentParsingMode, CfgIndentParsingMode;
    public final ModConfigSpec.IntValue CfgParserBaseCost, CfgMaxBlankLine;

    public HexParseConfigForge(ModConfigSpec.Builder builder) {
        CfgParseGreatSpells = builder.comment(DESCRIP_PARSE_GREAT).defineEnum("ParseGreatSpells", ParseGreatPatternMode.BY_SCROLL);
        CfgCommentParsingMode = builder.comment(DESCRIP_ENABLE_COMMENTS).defineEnum("CommentParsingMode", CommentParsingMode.MANUAL);
        CfgIndentParsingMode = builder.comment(DESCRIP_ENABLE_INDENTS).defineEnum("IndentParsingMode", CommentParsingMode.ALL);
        CfgMaxBlankLine = builder.comment(DESCRIP_MAX_BLANK_LINES).defineInRange("MaxBlankLines", 0, 0, Integer.MAX_VALUE);
        CfgAddIndentInsideMacro = builder.comment(DESCRIP_ADD_INDENT_INSIDE_MACRO).define("ExtendIndentInsideMacro", true);
        CfgAlwaysShortName = builder.comment(DESCRIP_ALWAYS_SHORT_NAME).define("AlwaysShortName", true);
        CfgParserBaseCost = builder.comment(DESCRIP_PARSER_BASE_COST).defineInRange("ParserBaseCost", 0, 0, 100000);
        CfgFairPlayPropNames = builder.comment(DESCRIP_FAIR_PLAY_PROP_NAMES).define("FairPlayPropNames", false);
        CfgShowColorfulNested = builder.comment(DESCRIP_COLORFUL_NESTED).define("ShowColorfulNested", true);
        CfgShowUnknownNBT = builder.comment(DESCRIP_SHOW_UNKNOWN_NBT).defineEnum("ShowUnknownNBT", UnknownNbtHandlingMode.KEEP_NBT);
        CfgSyncDisplayToClient = builder.comment(DESCRIP_SYNC_DISPLAY_TO_CLIENT).define("SyncDisplayToClient", false);
    }

    private static final Pair<HexParseConfigForge, ModConfigSpec> CFG_REGISTRY;

    static {
        CFG_REGISTRY = new ModConfigSpec.Builder().configure(HexParseConfigForge::new);
    }

    public static void register(ModContainer ctx) {
        bindConfigImp(CFG_REGISTRY.getKey());
        ctx.registerConfig(ModConfig.Type.COMMON, CFG_REGISTRY.getValue());
    }
}

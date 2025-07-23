package io.yukkuric.hexparse.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
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
        return null;
    }

    @Override
    public boolean showColorfulNested() {
        return CfgShowColorfulNested.get();
    }

    @Override
    public int parserBaseCost() {
        return CfgParserBaseCost.get();
    }

    public final ForgeConfigSpec.BooleanValue CfgShowColorfulNested;
    public final ForgeConfigSpec.EnumValue<ParseGreatPatternMode> CfgParseGreatSpells;
    public final ForgeConfigSpec.EnumValue<CommentParsingMode> CfgCommentParsingMode, CfgIndentParsingMode;
    public final ForgeConfigSpec.IntValue CfgParserBaseCost;

    public HexParseConfigForge(ForgeConfigSpec.Builder builder) {
        CfgParseGreatSpells = builder.comment(DESCRIP_PARSE_GREAT).defineEnum("ParseGreatSpells", ParseGreatPatternMode.BY_SCROLL);
        CfgCommentParsingMode = builder.comment(DESCRIP_ENABLE_COMMENTS).defineEnum("CommentParsingMode", CommentParsingMode.MANUAL);
        CfgIndentParsingMode = builder.comment(DESCRIP_ENABLE_INDENTS).defineEnum("IndentParsingMode", CommentParsingMode.ALL);
        CfgParserBaseCost = builder.comment(DESCRIP_PARSER_BASE_COST).defineInRange("ParserBaseCost", 0, 0, 100000);
        CfgShowColorfulNested = builder.comment(DESCRIP_COLORFUL_NESTED).define("ShowColorfulNested", true);
    }

    private static final Pair<HexParseConfigForge, ForgeConfigSpec> CFG_REGISTRY;

    static {
        CFG_REGISTRY = new ForgeConfigSpec.Builder().configure(HexParseConfigForge::new);
    }

    public static void register(ModLoadingContext ctx) {
        bindConfigImp(CFG_REGISTRY.getKey());
        ctx.registerConfig(ModConfig.Type.COMMON, CFG_REGISTRY.getValue());
    }
}

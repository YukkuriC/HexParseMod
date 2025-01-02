package io.yukkuric.hexparse.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import static io.yukkuric.hexparse.config.HexParseConfig.*;

public class HexParseConfigForge implements API {
    @Override
    public ParseGreatPatternMode canParseGreatPatterns() {
        return parseGreatEnabled.get();
    }

    @Override
    public boolean parseCommentsAndIndents() {
        return parseIndentsEnabled.get();
    }

    @Override
    public int parserBaseCost() {
        return parserBaseCostCfg.get();
    }

    public ForgeConfigSpec.BooleanValue parseIndentsEnabled;
    public ForgeConfigSpec.EnumValue<ParseGreatPatternMode> parseGreatEnabled;
    public ForgeConfigSpec.IntValue parserBaseCostCfg;

    public HexParseConfigForge(ForgeConfigSpec.Builder builder) {
        parseGreatEnabled = builder.comment(DESCRIP_PARSE_GREAT).defineEnum("ParseGreatSpells", ParseGreatPatternMode.BY_SCROLL);
        parseIndentsEnabled = builder.comment(DESCRIP_ENABLE_COMMENTS).define("ParseCommentsIndents", true);
        parserBaseCostCfg = builder.comment(DESCRIP_PARSER_BASE_COST).defineInRange("ParserBaseCost", 0, 0, 100000);
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

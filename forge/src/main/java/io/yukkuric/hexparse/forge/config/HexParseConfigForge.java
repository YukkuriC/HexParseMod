package io.yukkuric.hexparse.forge.config;

import io.yukkuric.hexparse.config.HexParseConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class HexParseConfigForge implements HexParseConfig.API {
    @Override
    public HexParseConfig.ParseGreatPatternMode canParseGreatPatterns() {
        return parseGreatEnabled.get();
    }

    @Override
    public boolean parseCommentsAndIndents() {
        return parseIndentsEnabled.get();
    }

    public ForgeConfigSpec.BooleanValue parseIndentsEnabled;
    public ForgeConfigSpec.EnumValue<HexParseConfig.ParseGreatPatternMode> parseGreatEnabled;

    public HexParseConfigForge(ForgeConfigSpec.Builder builder) {
        parseGreatEnabled = builder.comment(HexParseConfig.DESCRIP_PARSE_GREAT).defineEnum("ParseGreatSpells", HexParseConfig.ParseGreatPatternMode.BY_SCROLL);
        parseIndentsEnabled = builder.comment(HexParseConfig.DESCRIP_ENABLE_COMMENTS).define("ParseCommentsIndents", true);
    }

    private static final Pair<HexParseConfigForge, ForgeConfigSpec> CFG_REGISTRY;

    static {
        CFG_REGISTRY = new ForgeConfigSpec.Builder().configure(HexParseConfigForge::new);
    }

    public static void register(ModLoadingContext ctx) {
        HexParseConfig.bindConfigImp(CFG_REGISTRY.getKey());
        ctx.registerConfig(ModConfig.Type.COMMON, CFG_REGISTRY.getValue());
    }
}

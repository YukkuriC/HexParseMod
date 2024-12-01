package io.yukkuric.hexparse.forge.config;

import io.yukkuric.hexparse.config.HexParseConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class HexParseConfigForge implements HexParseConfig.API {
    @Override
    public boolean canParseGreatPatterns() {
        return parseGreatEnabled.get();
    }

    @Override
    public boolean parseCommentsAndIndents() {
        return parseIndentsEnabled.get();
    }

    public ForgeConfigSpec.BooleanValue parseGreatEnabled, parseIndentsEnabled;

    public HexParseConfigForge(ForgeConfigSpec.Builder builder) {
        parseGreatEnabled = builder.comment("can directly parse great spell patterns, without scrolls").define("ParseGreatSpells", true);
        parseIndentsEnabled = builder.comment("enable comments and auto parse indents into comment iota for display").define("ParseCommentsIndents", true);
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

package io.yukkuric.hexparse.fabric.config;

import io.yukkuric.hexparse.HexParse;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import static io.yukkuric.hexparse.config.HexParseConfig.*;

@Config(name = HexParse.MOD_ID)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class HexParseConfigFabric extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    private final Common common = new Common();

    public static void setup() {
        AutoConfig.register(HexParseConfigFabric.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var instance = AutoConfig.getConfigHolder(HexParseConfigFabric.class).getConfig();
        bindConfigImp(instance.common);
    }

    @Config(name = "common")
    public static class Common implements API, ConfigData {
        @Comment(DESCRIP_PARSE_GREAT)
        private ParseGreatPatternMode parseGreatSpells = ParseGreatPatternMode.BY_SCROLL;
        @Comment(DESCRIP_ENABLE_COMMENTS)
        private boolean parseCommentsIndents = true;
        @Comment(DESCRIP_PARSER_BASE_COST)
        private int parserBaseCost = 0;

        @Override
        public ParseGreatPatternMode canParseGreatPatterns() {
            return parseGreatSpells;
        }

        @Override
        public boolean parseCommentsAndIndents() {
            return parseCommentsIndents;
        }

        @Override
        public int parserBaseCost() {
            return parserBaseCost;
        }
    }
}
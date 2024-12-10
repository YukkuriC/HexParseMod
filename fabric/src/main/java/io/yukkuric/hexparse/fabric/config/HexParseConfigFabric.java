package io.yukkuric.hexparse.fabric.config;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.config.HexParseConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = HexParse.MOD_ID)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class HexParseConfigFabric extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    private final Common common = new Common();

    public static void setup() {
        AutoConfig.register(HexParseConfigFabric.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var instance = AutoConfig.getConfigHolder(HexParseConfigFabric.class).getConfig();
        HexParseConfig.bindConfigImp(instance.common);
    }

    @Config(name = "common")
    public static class Common implements HexParseConfig.API, ConfigData {
        @Comment(HexParseConfig.DESCRIP_PARSE_GREAT)
        private HexParseConfig.ParseGreatPatternMode parseGreatSpells = HexParseConfig.ParseGreatPatternMode.BY_SCROLL;
        @Comment(HexParseConfig.DESCRIP_ENABLE_COMMENTS)
        private boolean parseCommentsIndents = true;

        @Override
        public HexParseConfig.ParseGreatPatternMode canParseGreatPatterns() {
            return parseGreatSpells;
        }

        @Override
        public boolean parseCommentsAndIndents() {
            return parseCommentsIndents;
        }
    }
}
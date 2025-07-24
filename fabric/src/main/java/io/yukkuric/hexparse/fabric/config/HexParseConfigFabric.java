package io.yukkuric.hexparse.fabric.config;

import io.yukkuric.hexparse.HexParse;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import static io.yukkuric.hexparse.config.HexParseConfig.*;

@Config(name = HexParse.MOD_ID)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class HexParseConfigFabric extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @Gui.TransitiveObject
    private final Common common = new Common();

    public static void setup() {
        AutoConfig.register(HexParseConfigFabric.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var instance = AutoConfig.getConfigHolder(HexParseConfigFabric.class).getConfig();
        bindConfigImp(instance.common);
    }

    @Config(name = "common")
    public static class Common implements API, ConfigData {
        @Comment(DESCRIP_PARSE_GREAT)
        @Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        private ParseGreatPatternMode parseGreatSpells = ParseGreatPatternMode.BY_SCROLL;
        @Comment(DESCRIP_ENABLE_COMMENTS)
        @Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        private CommentParsingMode commentParsingMode = CommentParsingMode.MANUAL;
        @Comment(DESCRIP_ENABLE_INDENTS)
        @Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
        private CommentParsingMode indentParsingMode = CommentParsingMode.MANUAL;
        @Comment(DESCRIP_PARSER_BASE_COST)
        private int parserBaseCost = 0;
        @Comment(DESCRIP_COLORFUL_NESTED)
        private boolean showColorfulNested = true;

        @Override
        public ParseGreatPatternMode canParseGreatPatterns() {
            return parseGreatSpells;
        }

        @Override
        public CommentParsingMode getCommentParsingMode() {
            return commentParsingMode;
        }
        @Override
        public CommentParsingMode getIndentParsingMode() {
            return indentParsingMode;
        }

        @Override
        public boolean showColorfulNested() {
            return showColorfulNested;
        }

        @Override
        public int parserBaseCost() {
            return parserBaseCost;
        }
    }
}
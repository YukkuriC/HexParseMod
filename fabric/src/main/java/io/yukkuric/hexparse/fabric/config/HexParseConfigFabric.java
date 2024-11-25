package io.yukkuric.hexparse.fabric.config;

import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.config.HexParseConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

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
        @ConfigEntry.Gui.Tooltip
        private boolean canParseGreatSpells = true;

        @Override
        public boolean canParseGreatPatterns() {
            return canParseGreatSpells;
        }
    }
}
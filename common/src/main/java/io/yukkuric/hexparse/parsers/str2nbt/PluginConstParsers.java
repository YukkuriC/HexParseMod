package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix;
import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Regex;

public class PluginConstParsers {
    public static BaseConstParser TO_ENTITY_TYPE = new Prefix("type/entity_") {
        @Override
        public CompoundTag parse(String node) {
            var type = node.substring(node.indexOf('_') + 1);
            if (type.indexOf(':') < 0) type = "minecraft:" + type;
            return PluginIotaFactory.makeEntityType(type);
        }
    };
    public static BaseConstParser TO_IOTA_TYPE = new Regex("^type(\\/iota)?_") {
        @Override
        public CompoundTag parse(String node) {
            var type = node.substring(node.indexOf('_') + 1);
            if (type.indexOf(':') < 0) type = "hexcasting:" + type;
            return PluginIotaFactory.makeIotaType(type);
        }
    };
    public static BaseConstParser TO_STRING = new Prefix("str_") {
        @Override
        public CompoundTag parse(String node) {
            var str = node.substring(node.indexOf('_') + 1);
            return PluginIotaFactory.makeString(str);
        }
    };
}

package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;

import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix;
import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Regex;

public class PluginConstParsers {
    public static BaseConstParser TO_ENTITY_TYPE = new Resource("type/entity_", PluginIotaFactory::makeEntityType);
    public static BaseConstParser TO_ITEM_TYPE = new Resource("type/item_", PluginIotaFactory::makeItemType);
    public static BaseConstParser TO_BLOCK_TYPE = new Resource("type/block_", PluginIotaFactory::makeBlockType);

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

    static class Resource extends Prefix {
        Function<String, CompoundTag> subParse;

        public Resource(String prefix, Function<String, CompoundTag> sub) {
            super(prefix);
            this.subParse = sub;
        }

        @Override
        public CompoundTag parse(String node) {
            var type = node.substring(node.indexOf('_') + 1);
            if (type.indexOf(':') < 0) type = "minecraft:" + type;
            return subParse.apply(type);
        }
    }
}

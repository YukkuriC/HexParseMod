package io.yukkuric.hexparse.parsers.str2nbt.plugins;

import at.petrak.hexcasting.api.misc.MediaConstants;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
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

    public static BaseConstParser TO_PROPERTY = new Regex("^prop(erty)?_") {
        @Override
        public CompoundTag parse(String node) {
            var str = node.substring(node.indexOf('_') + 1);
            var packed = new CompoundTag();
            packed.putString("name", StringProcessors.APPEND_UNDERLINE.apply(str));
            return PluginIotaFactory.makeProperty(packed);
        }

        @Override
        public int getCost() {
            return super.getCost() + (int) MediaConstants.CRYSTAL_UNIT;
        }
    };

    public static BaseConstParser TO_STRING_LIT = new Prefix("\"") {
        @Override
        public CompoundTag parse(String node) {
            // remove the quotes
            node = node.substring(1, node.length() - 1);


            var unescapedString = new StringBuilder();
            var currentPartStartIndex = 0;


            // node.length() - 1 so that the charAt(index + 1) is always safe
            for (var index = 0; index < node.length() - 1; index++) {
                if (node.charAt(index) == '\\') {
                    unescapedString.append(node, currentPartStartIndex, index);
                    unescapedString.append(switch (node.charAt(index + 1)) {
                        case 'n' -> '\n';
                        case '\\' -> '\\';
                        case '"' -> '"';
                        default -> throw new IllegalArgumentException("invalid escape sequence");
                    });
                    currentPartStartIndex = index + 2;
                    // skip the escaped char
                    index++;
                }
            }
            unescapedString.append(node, currentPartStartIndex, node.length());

            if (node.charAt(node.length() - 1) == '\\' && (node.length() < 2 ||  node.charAt(node.length() - 2) != '\\')) {
                throw new IllegalArgumentException("illegal escape pattern, trailing backslash");
                // illegal escape
            }


            return PluginIotaFactory.makeString(unescapedString.toString());
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

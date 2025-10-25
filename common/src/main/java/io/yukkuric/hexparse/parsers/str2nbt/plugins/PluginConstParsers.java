package io.yukkuric.hexparse.parsers.str2nbt.plugins;

import at.petrak.hexcasting.api.misc.MediaConstants;
import io.yukkuric.hexparse.misc.StringEscaper;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

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
    public static BaseConstParser TO_MATRIX = new Regex("^mat(rix)?_") {
        @Override
        public CompoundTag parse(String node) {
            return PluginIotaFactory.makeMatrix(node.split("_"));
        }
    };

    public static class ToProperty extends Regex {
        protected ToProperty(String regex) {
            super(regex);
        }
        String getPropValue(String str) {
            return StringProcessors.APPEND_UNDERLINE.apply(str);
        }
        @Override
        public CompoundTag parse(String node) {
            var str = node.substring(node.indexOf('_') + 1);
            var packed = new CompoundTag();
            packed.putString("name", getPropValue(str));
            return PluginIotaFactory.makeProperty(packed);
        }
        @Override
        public int getCost() {
            return super.getCost() + (int) MediaConstants.CRYSTAL_UNIT;
        }

        public static class Private extends ToProperty implements IPlayerBinder {
            private ServerPlayer owner;

            protected Private(String regex) {
                super(regex);
            }
            @Override
            public void BindPlayer(ServerPlayer p) {
                owner = p;
            }
            @Override
            String getPropValue(String str) {
                return "[%s]@%s".formatted(owner.getScoreboardName(), str); // 1.19 no inline :(
            }
        }
    }
    public static BaseConstParser TO_PROPERTY = new ToProperty("^prop(erty)?_");
    public static BaseConstParser TO_MY_PROPERTY = new ToProperty.Private("^myprop_");

    public static BaseConstParser TO_STRING_LIT = new Prefix("\"") {
        @Override
        public CompoundTag parse(String node) {
            // remove the quotes
            node = node.substring(1, node.length() - 1);


            var string = StringEscaper.Companion.unescape(node);


            return PluginIotaFactory.makeString(string);
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

    public static BaseConstParser TO_IDENTIFIER = new Resource("id_", PluginIotaFactory::makeResLoc);
}

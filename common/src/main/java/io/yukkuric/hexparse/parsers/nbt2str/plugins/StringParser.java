package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;

public class StringParser implements INbt2Str {
    String type, prefix;

    StringParser(String type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, type);
    }

    @Override
    public String parse(CompoundTag node) {
        return this.prefix + node.getString(HexIotaTypes.KEY_DATA);
    }

    public static StringParser IOTA = new StringParser(PluginIotaFactory.TYPE_IOTA_TYPE, "type_");
    public static StringParser ENTITY = new StringParser(PluginIotaFactory.TYPE_ENTITY_TYPE, "type/entity_");
    public static StringParser STRING = new StringParser(PluginIotaFactory.TYPE_STRING, "str_");
}

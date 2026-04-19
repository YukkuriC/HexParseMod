package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.misc.StringProcessors.*;

public class StringParser implements INbt2Str {
    String type, prefix;
    F[] processors;

    StringParser(String type, String prefix, F... processors) {
        this.type = type;
        this.prefix = prefix;
        this.processors = processors;
    }

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, type);
    }

    @Override
    public String parse(CompoundTag node) {
        var raw = node.getString(HexIotaTypes.KEY_DATA);
        for (var f : processors) raw = f.apply(raw);
        return this.prefix + raw;
    }

    public static StringParser IOTA = new StringParser(PluginIotaFactory.TYPE_IOTA_TYPE, "type_", OMIT_HEX);
    public static StringParser ENTITY = new StringParser(PluginIotaFactory.TYPE_ENTITY_TYPE, "type/entity_", OMIT_MC);
    public static StringParser STRING = new StringParser(PluginIotaFactory.TYPE_STRING, "str_");
}

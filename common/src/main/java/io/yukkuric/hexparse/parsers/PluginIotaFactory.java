package io.yukkuric.hexparse.parsers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;

public class PluginIotaFactory extends IotaFactory {
    // hexal iotas
    public static final String TYPE_IOTA_TYPE = "hexal:iota_type";
    public static final String TYPE_ENTITY_TYPE = "hexal:entity_type";
    public static final String TYPE_STRING = "moreiotas:string";

    public static CompoundTag makeIotaType(String type) {
        return _makeType(TYPE_IOTA_TYPE, StringTag.valueOf(type));
    }

    public static CompoundTag makeEntityType(String type) {
        return _makeType(TYPE_ENTITY_TYPE, StringTag.valueOf(type));
    }

    public static CompoundTag makeString(String data) {
        return _makeType(TYPE_STRING, StringTag.valueOf(data));
    }
}

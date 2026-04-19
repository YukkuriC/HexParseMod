package io.yukkuric.hexparse.parsers.nbt2str.unsafe.hexal;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;
import ram.talia.hexal.api.mediafieditems.MediafiedItemManager;

public class MoteParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, PluginIotaFactory.TYPE_MOTE);
    }

    @Override
    public String parse(CompoundTag node) {
        node = node.getCompound(HexIotaTypes.KEY_DATA);
        var uuid = node.getUUID(MediafiedItemManager.Index.TAG_STORAGE);
        var i = node.getInt(MediafiedItemManager.Index.TAG_INDEX);
        return "mote_%s_%s".formatted(uuid, i);
    }

    public static MoteParser INSTANCE = new MoteParser();
}

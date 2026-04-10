package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.HexRegistries;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.tags.TagKey;

public class HexParseTags {
    public static final TagKey<IotaType<?>> NBT_PARSING_FORBIDDEN = TagKey.create(HexRegistries.IOTA_TYPE, HexParse.modLoc("nbt_parsing_forbidden"));
}

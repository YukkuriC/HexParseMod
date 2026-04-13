package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class HexParseTags {
    static final ResourceKey<Registry<IotaType<?>>> IOTA_TYPE_KEY = ResourceKey.createRegistryKey(HexAPI.modLoc("iota_type"));

    public static final TagKey<IotaType<?>> NBT_PARSING_FORBIDDEN = TagKey.create(IOTA_TYPE_KEY, HexParse.modLoc("nbt_parsing_forbidden"));
}

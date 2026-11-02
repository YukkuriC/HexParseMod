package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.*;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.*;

public class IotaFactory {
    public static final String TYPE_LIST = HexAPI.MOD_ID + ":list";
    public static final String TYPE_PATTERN = HexAPI.MOD_ID + ":pattern";
    public static final String TYPE_DOUBLE = HexAPI.MOD_ID + ":double";
    public static final String TYPE_VECTOR = HexAPI.MOD_ID + ":vec3";
    public static final String TYPE_ENTITY = HexAPI.MOD_ID + ":entity";
    public static final String TYPE_BOOLEAN = HexAPI.MOD_ID + ":boolean";
    public static final String TYPE_NULL = HexAPI.MOD_ID + ":null";
    public static final String TYPE_GARBAGE = HexAPI.MOD_ID + ":garbage";

    public static final String GREAT_PLACEHOLDER_PREFIX = "<";
    public static final String GREAT_PLACEHOLDER_POSTFIX = "?>";

    public static CompoundTag makeType(String type, Tag data, String dataKey) {
        var res = new CompoundTag();
        res.putString(KEY_TYPE, type);
        res.put(dataKey, data);
        return res;
    }
    public static CompoundTag makeType(String type, Tag data) {
        return makeType(type, data, KEY_DATA);
    }

    public static CompoundTag makeList(ListTag data) {
        return makeType(TYPE_LIST, data, "list");
    }

    public static CompoundTag makePattern(String angles, HexDir start) {
        return makePattern(HexPattern.fromAngleString(angles, start, false));
    }
    public static CompoundTag makePattern(HexPattern pattern) {
        return toNbt(new PatternIota(pattern));
    }

    public static CompoundTag makeComment(String comment) {
        return makeType(CommentIotaType.TYPE_ID, StringTag.valueOf(comment), "comment");
    }

    public static boolean isGreatPatternPlaceholder(String node) {
        return node.startsWith(GREAT_PLACEHOLDER_PREFIX) && node.endsWith(GREAT_PLACEHOLDER_POSTFIX);
    }

    public static String makeUnknownGreatPatternText(String id) {
        return GREAT_PLACEHOLDER_PREFIX + id + GREAT_PLACEHOLDER_POSTFIX;
    }

    public static CompoundTag makeUnknownGreatPattern(String id) {
        return makeComment(makeUnknownGreatPatternText(id));
    }

    public static CompoundTag makeTab(int num) {
        return makeComment("\n" + " ".repeat(num));
    }

    public static CompoundTag makeNum(Double num) {
        return makeType(TYPE_DOUBLE, DoubleTag.valueOf(num));
    }
}

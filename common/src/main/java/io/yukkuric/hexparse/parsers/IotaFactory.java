package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.*;

import java.util.*;

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

    static final Map<Character, Byte> ANGLE_MAP = new HashMap<>() {
        {
            put('w', (byte) 0);
            put('e', (byte) 1);
            put('d', (byte) 2);
            put('s', (byte) 3);
            put('a', (byte) 4);
            put('q', (byte) 5);
        }
    };

    public static CompoundTag makeType(String type, Tag data) {
        var res = new CompoundTag();
        res.putString(HexIotaTypes.KEY_TYPE, type);
        res.put(HexIotaTypes.KEY_DATA, data);
        return res;
    }

    public static CompoundTag makeList(ListTag data) {
        return makeType(TYPE_LIST, data);
    }

    public static CompoundTag makePattern(String angles, HexDir start) {
        var angleArray = new ArrayList<Byte>();
        for (var chr : angles.toCharArray()) { // skip fromAngles check
            if (ANGLE_MAP.containsKey(chr)) angleArray.add(ANGLE_MAP.get(chr));
            else
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.illegal_pattern_angle", chr, angles));
        }
        var pattern = new CompoundTag();
        pattern.putByte("start_dir", (byte) (start.ordinal()));
        pattern.putByteArray("angles", angleArray);
        return makeType(TYPE_PATTERN, pattern);
    }

    public static CompoundTag makeComment(String comment) {
        return makeType(CommentIotaType.TYPE_ID, StringTag.valueOf(comment));
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

package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IotaFactory {
    public static final String TYPE_LIST = HexAPI.MOD_ID + ":list";
    public static final String TYPE_PATTERN = HexAPI.MOD_ID + ":pattern";
    public static final String TYPE_DOUBLE = HexAPI.MOD_ID + ":double";
    public static final String TYPE_VECTOR = HexAPI.MOD_ID + ":vec3";

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

    static CompoundTag _makeType(String type, Tag data) {
        var res = new CompoundTag();
        res.putString(HexIotaTypes.KEY_TYPE, type);
        res.put(HexIotaTypes.KEY_DATA, data);
        return res;
    }

    public static CompoundTag makeList(ListTag data) {
        return _makeType(TYPE_LIST, data);
    }

    public static CompoundTag makePattern(String angles, HexDir start) {
        var angleArray = new ArrayList<Byte>();
        for (var chr : angles.toCharArray()) { // skip fromAngles check
            if (ANGLE_MAP.containsKey(chr)) angleArray.add(ANGLE_MAP.get(chr));
            else throw new IllegalArgumentException(String.format("illegal char '%s' in sequence \"%s\"", chr, angles));
        }
        var pattern = new CompoundTag();
        pattern.putByte("start_dir", (byte) (start.ordinal()));
        pattern.putByteArray("angles", angleArray);
        return _makeType(TYPE_PATTERN, pattern);
    }

    public static CompoundTag makeComment(String comment) {
        return _makeType(CommentIotaType.TYPE_ID, StringTag.valueOf(comment));
    }

    public static CompoundTag makeTab(int num) {
        return makeComment("\n" + " ".repeat(num));
    }

    public static CompoundTag makeNum(Double num) {
        return _makeType(TYPE_DOUBLE, DoubleTag.valueOf(num));
    }
}

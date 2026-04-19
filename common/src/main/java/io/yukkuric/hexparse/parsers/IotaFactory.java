package io.yukkuric.hexparse.parsers;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.*;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.CommentIota;

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

    static final Map<Character, HexAngle> ANGLE_MAP = new HashMap<>() {
        {
            put('w', HexAngle.FORWARD);
            put('e', HexAngle.RIGHT);
            put('d', HexAngle.RIGHT_BACK);
            put('s', HexAngle.BACK);
            put('a', HexAngle.LEFT_BACK);
            put('q', HexAngle.LEFT);
        }
    };

    public static Iota makePattern(String angles, HexDir start) {
        var angleArray = new ArrayList<HexAngle>();
        for (var chr : angles.toCharArray()) { // skip fromAngles check
            if (ANGLE_MAP.containsKey(chr)) angleArray.add(ANGLE_MAP.get(chr));
            else
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.illegal_pattern_angle", chr, angles));
        }
        var pattern = new HexPattern(start, angleArray);
        return new PatternIota(pattern);
    }

    public static Iota makeComment(String comment) {
        return new CommentIota(comment);
    }

    public static boolean isGreatPatternPlaceholder(String node) {
        return node.startsWith(GREAT_PLACEHOLDER_PREFIX) && node.endsWith(GREAT_PLACEHOLDER_POSTFIX);
    }

    public static String makeUnknownGreatPatternText(String id) {
        return GREAT_PLACEHOLDER_PREFIX + id + GREAT_PLACEHOLDER_POSTFIX;
    }

    public static Iota makeUnknownGreatPattern(String id) {
        return makeComment(makeUnknownGreatPatternText(id));
    }

    public static Iota makeTab(int num) {
        return makeComment("\n" + " ".repeat(num));
    }

    public static Iota makeNum(Double num) {
        return new DoubleIota(num);
    }
}

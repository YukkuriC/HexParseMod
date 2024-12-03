package io.yukkuric.hexparse_client.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.math.HexDir;
import io.yukkuric.hexparse_client.misc.NumEvaluatorBrute;
import io.yukkuric.hexparse_client.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse_client.parsers.ParserMain.IGNORED;
import static io.yukkuric.hexparse_client.parsers.str2nbt.BaseConstParser.Prefix;
import static io.yukkuric.hexparse_client.parsers.str2nbt.BaseConstParser.Regex;

public class ConstParsers {
    // prefix
    public static BaseConstParser TO_TAB = new Prefix("tab") {
        @Override
        public CompoundTag parse(String node) {
            return IGNORED;
        }
    };
    public static BaseConstParser TO_COMMENT = new Prefix("comment_") {
        @Override
        public CompoundTag parse(String node) {
            return IGNORED;
        }
    };
    public static BaseConstParser TO_VEC = new Prefix("vec") {
        @Override
        public CompoundTag parse(String node) {
            return IGNORED;
        }
    };

    public static BaseConstParser TO_NUM_PATTERN = new Prefix("num") {
        @Override
        public CompoundTag parse(String node) {
            double num = 0;
            try {
                num = Double.parseDouble(node.substring(4));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
            }
            var seq = NumEvaluatorBrute.getAnglesFromNum(num);
            return IotaFactory.makePattern(seq, num < 0 ? HexDir.NORTH_EAST : HexDir.SOUTH_EAST);
        }
    };

    // regex
    public static BaseConstParser TO_NUM = new Regex("^[0-9.\\-]+(e[0-9.\\-]+)?$") {
        @Override
        public CompoundTag parse(String node) {
            return IGNORED;
        }
    };
    public static BaseConstParser TO_RAW_PATTERN = new Regex("^_[wedsaq]*$") {
        @Override
        public CompoundTag parse(String node) {
            return IotaFactory.makePattern(node.substring(1), HexDir.EAST);
        }
    };
    public static BaseConstParser TO_MASK = new Regex("^mask_[-v]+$") {
        @Override
        public CompoundTag parse(String node) {
            var seq = new StringBuilder();
            var line = true;
            var start = HexDir.EAST;
            if (node.charAt(5) == 'v') {
                line = false;
                seq.append('a');
                start = HexDir.SOUTH_EAST;
            }
            for (var c : node.substring(6).toCharArray()) {
                if (c == '-') {
                    seq.append(line ? 'w' : 'e');
                    line = true;
                } else {
                    seq.append(line ? "ea" : "da");
                    line = false;
                }
            }
            return IotaFactory.makePattern(seq.toString(), start);
        }
    };
}

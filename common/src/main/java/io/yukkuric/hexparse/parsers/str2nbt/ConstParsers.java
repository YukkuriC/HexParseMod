package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.misc.IotaFactory;
import io.yukkuric.hexparse.misc.NumEvaluatorBrute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.*;

public class ConstParsers {
    // prefix
    public static BaseConstParser TO_TAB = new Prefix("tab") {
        @Override
        public CompoundTag parse(String node) {
            int indent = 0;
            try {
                indent = Integer.parseInt(node.substring(4));
            } catch (NumberFormatException e) {
            }
            return IotaFactory.makeTab(indent);
        }
    };
    public static BaseConstParser TO_COMMENT = new Prefix("comment") {
        @Override
        public CompoundTag parse(String node) {
            return IotaFactory.makeComment(node.substring(8));
        }
    };
    public static BaseConstParser TO_VEC = new Prefix("vec") {
        @Override
        public CompoundTag parse(String node) {
            var frags = node.split("_");
            var axes = new double[3];
            for (var i = 1; i <= 3; i++) {
                axes[i - 1] = 0;
                if (i >= frags.length) continue;
                try {
                    axes[i - 1] = Double.parseDouble(frags[i]);
                } catch (NumberFormatException e) {
                }
            }
            return HexIotaTypes.serialize(new Vec3Iota(new Vec3(axes[0], axes[1], axes[2])));
        }
    };
    public static BaseConstParser TO_NUM_PATTERN = new Prefix("num") {
        @Override
        public CompoundTag parse(String node) {
            double num = 0;
            try {
                num = Double.parseDouble(node.substring(4));
            } catch (NumberFormatException e) {
            }
            var seq = NumEvaluatorBrute.getAnglesFromNum(num);
            return IotaFactory.makePattern(seq, num < 0 ? HexDir.NORTH_EAST : HexDir.SOUTH_EAST);
        }
    };

    // regex
    public static BaseConstParser TO_NUM = new Regex("^[0-9.\\-]+(e[0-9.\\-]+)?$") {
        @Override
        public CompoundTag parse(String node) {
            try {
                return IotaFactory.makeNum(Double.parseDouble(node));
            } catch (NumberFormatException e) {
                return IotaFactory.makeNum(Double.NaN);
            }
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
            var line = false;
            var start = HexDir.EAST;
            if (node.charAt(5) == 'v') {
                line = false;
                seq.append('a');
                start = HexDir.SOUTH_EAST;
            }
            for (var c : node.substring(5).toCharArray()) {
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

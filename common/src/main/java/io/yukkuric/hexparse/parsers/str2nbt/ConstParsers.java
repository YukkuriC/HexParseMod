package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.misc.IotaFactory;
import net.minecraft.nbt.CompoundTag;

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
}

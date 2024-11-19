package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.misc.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.regex.Pattern;

public class ToNum extends RegExpMatcher {
    public ToNum() {
        super(Pattern.compile("^[0-9.\\-]+(e[0-9.\\-]+)?$"));
    }

    @Override
    public CompoundTag parse(String node) {
        try {
            return IotaFactory.makeNum(Double.parseDouble(node));
        } catch (NumberFormatException e) {
            return IotaFactory.makeNum(Double.NaN);
        }
    }
}

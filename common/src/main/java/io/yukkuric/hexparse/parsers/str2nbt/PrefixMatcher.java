package io.yukkuric.hexparse.parsers.str2nbt;


public abstract class PrefixMatcher implements IStr2Nbt {
    String prefix;

    PrefixMatcher(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean match(String node) {
        return node.startsWith(prefix);
    }
}

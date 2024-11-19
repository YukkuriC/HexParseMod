package io.yukkuric.hexparse.parsers.str2nbt;


import java.util.regex.Pattern;

public abstract class RegExpMatcher implements IStr2Nbt {
    Pattern matcher;

    RegExpMatcher(Pattern matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(String node) {
        return matcher.matcher(node).find();
    }
}

package io.yukkuric.hexparse.parsers.str2nbt;

import java.util.regex.Pattern;

public abstract class BaseConstParser implements IStr2Nbt {
    public static abstract class Prefix extends BaseConstParser {
        String prefix;

        Prefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean match(String node) {
            return node.startsWith(this.prefix);
        }
    }

    public static abstract class Regex extends BaseConstParser {
        Pattern regex;

        Regex(String regex) {
            this.regex = Pattern.compile(regex);
        }

        @Override
        public boolean match(String node) {
            return regex.matcher(node).find();
        }
    }
}

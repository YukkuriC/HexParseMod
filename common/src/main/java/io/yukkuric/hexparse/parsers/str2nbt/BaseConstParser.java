package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.config.HexParseConfig;

import java.util.regex.Pattern;

public abstract class BaseConstParser implements IStr2Nbt {
    public static abstract class Prefix extends BaseConstParser {
        String prefix;

        protected Prefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean match(String node) {
            return node.startsWith(this.prefix);
        }
    }

    public static abstract class Regex extends BaseConstParser {
        Pattern regex;

        protected Regex(String regex) {
            this.regex = Pattern.compile(regex);
        }

        @Override
        public boolean match(String node) {
            return regex.matcher(node).find();
        }
    }

    public static abstract class Comment extends Prefix {
        Comment(String prefix) {
            super(prefix);
        }

        @Override
        public int getCost() {
            return 0;
        }

        @Override
        public boolean ignored() {
            return HexParseConfig.getCommentParsingMode() == HexParseConfig.CommentParsingMode.DISABLED;
        }
    }
}

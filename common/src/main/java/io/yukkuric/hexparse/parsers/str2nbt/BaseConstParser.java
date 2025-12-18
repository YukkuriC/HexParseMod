package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.regex.Pattern;

public abstract class BaseConstParser implements IStr2Nbt {
    public static abstract class Prefix extends BaseConstParser {
        String[] prefix;

        public Prefix(String... prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean match(String node) {
            for (var p : prefix) if (node.startsWith(p)) return true;
            return false;
        }
    }

    public static abstract class Regex extends BaseConstParser {
        Pattern regex;

        public Regex(String regex) {
            this.regex = Pattern.compile(regex);
        }

        @Override
        public boolean match(String node) {
            return regex.matcher(node).find();
        }
    }

    public static abstract class Comment extends Prefix {
        public Comment(String... prefix) {
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

        public static class Indent extends Comment {
            public Indent() {
                super("tab", "indent");
            }
            @Override
            public CompoundTag parse(String node) {
                int indent = getIndent(node);
                return IotaFactory.makeTab(indent);
            }
            @Override
            public boolean ignored() {
                return HexParseConfig.getIndentParsingMode() == HexParseConfig.CommentParsingMode.DISABLED;
            }
            public int getIndent(String node) {
                try {
                    return Integer.parseInt(node.substring(4));
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    return 0;
                }
            }
        }
    }
}

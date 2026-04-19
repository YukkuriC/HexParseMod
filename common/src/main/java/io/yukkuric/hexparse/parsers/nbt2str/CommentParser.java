package io.yukkuric.hexparse.parsers.nbt2str;

import io.yukkuric.hexparse.hooks.CommentIota;
import io.yukkuric.hexparse.parsers.IotaFactory;

import java.util.regex.Pattern;

public class CommentParser implements INbt2Str<CommentIota> {
    public static Pattern INDENT = Pattern.compile("^\\n\\s*$");

    @Override
    public String parse(CommentIota iota) {
        var content = iota.comment;
        if (content.startsWith("\"")) return "/*" + content.substring(1, content.length() - 1) + "*/";
        if (INDENT.matcher(content).find()) return content;
        if (content.startsWith(IotaFactory.GREAT_PLACEHOLDER_PREFIX) && content.endsWith(IotaFactory.GREAT_PLACEHOLDER_POSTFIX))
            return content.substring(IotaFactory.GREAT_PLACEHOLDER_PREFIX.length(), content.length() - IotaFactory.GREAT_PLACEHOLDER_POSTFIX.length());
        return "comment_" + content;
    }
    @Override
    public Class<CommentIota> getType() {
        return CommentIota.class;
    }
}

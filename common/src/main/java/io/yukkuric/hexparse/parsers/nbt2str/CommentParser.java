package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.regex.Pattern;

public class CommentParser implements INbt2Str {
    static Pattern INDENT = Pattern.compile("^\\n\\s*$");

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, CommentIotaType.TYPE_ID);
    }

    @Override
    public String parse(CompoundTag node) {
        var content = node.getString(HexIotaTypes.KEY_DATA);
        if (content.startsWith("\"")) return "/*" + content.substring(1, content.length() - 1) + "*/";
        if (INDENT.matcher(content).find()) return content;
        if (content.startsWith(IotaFactory.GREAT_PLACEHOLDER_PREFIX) && content.endsWith(IotaFactory.GREAT_PLACEHOLDER_POSTFIX))
            return content.substring(IotaFactory.GREAT_PLACEHOLDER_PREFIX.length(), content.length() - IotaFactory.GREAT_PLACEHOLDER_POSTFIX.length());
        return "comment_" + content;
    }
}

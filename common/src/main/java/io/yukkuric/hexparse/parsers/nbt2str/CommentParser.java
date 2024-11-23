package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.CompoundTag;

import java.util.regex.Pattern;

public class CommentParser implements INbt2Str {
    static Pattern INDENT = Pattern.compile("^\\n\\s+$");

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, CommentIotaType.TYPE_ID);
    }

    @Override
    public String parse(CompoundTag node) {
        var content = node.getString(HexIotaTypes.KEY_DATA);
        if (INDENT.matcher(content).find()) return content;
        return "comment_" + content;
    }
}

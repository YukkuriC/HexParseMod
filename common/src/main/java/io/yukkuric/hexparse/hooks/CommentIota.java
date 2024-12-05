package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class CommentIota extends PatternIota {
    String comment;

    public CommentIota(String comment) {
        super(CommentIotaType.INSTANCE, CommentIotaType.COMMENT_PATTERN);
        this.comment = comment;
    }

    public Tag serialize() {
        return StringTag.valueOf(comment);
    }
}

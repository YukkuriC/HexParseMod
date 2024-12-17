package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CommentIota extends PatternIota {
    String comment;

    public CommentIota(String comment) {
        super(CommentIotaType.INSTANCE, CommentIotaType.COMMENT_PATTERN);
        this.comment = comment;
    }

    public Tag serialize() {
        return StringTag.valueOf(comment);
    }

    public CastResult execute(CastingVM vm, ServerLevel world, SpellContinuation continuation) {
        return new CastResult(this, continuation, vm.getImage(), List.of(), ResolvedPatternType.ESCAPED, HexEvalSounds.NOTHING);
    }
}

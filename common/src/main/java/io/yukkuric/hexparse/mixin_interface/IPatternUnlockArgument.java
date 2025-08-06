package io.yukkuric.hexparse.mixin_interface;

import at.petrak.hexcasting.common.command.PatternResLocArgument;
import com.mojang.brigadier.context.CommandContext;

import java.util.function.BiFunction;

public interface IPatternUnlockArgument {
    void setPredicate(UnlockPredicateType predicate);

    static PatternResLocArgument get(UnlockPredicateType predicate) {
        var orig = PatternResLocArgument.id();
        IPatternUnlockArgument.class.cast(orig).setPredicate(predicate);
        return orig;
    }

    public interface UnlockPredicateType extends BiFunction<CommandContext<?>, String, Boolean> {
    }
}

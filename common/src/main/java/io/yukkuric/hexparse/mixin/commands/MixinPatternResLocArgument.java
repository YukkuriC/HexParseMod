package io.yukkuric.hexparse.mixin.commands;

import at.petrak.hexcasting.common.command.PatternResLocArgument;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.yukkuric.hexparse.mixin_interface.IPatternUnlockArgument;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Mixin(PatternResLocArgument.class)
public class MixinPatternResLocArgument implements IPatternUnlockArgument {
    private UnlockPredicateType isValid = null;
    @Override
    public void setPredicate(UnlockPredicateType predicate) {
        isValid = predicate;
    }

    @WrapOperation(method = "listSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/SharedSuggestionProvider;suggest(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;"))
    CompletableFuture<Suggestions> hookPatternList(Iterable<String> patterns, SuggestionsBuilder suggestionsBuilder, Operation<CompletableFuture<Suggestions>> original, @Local(argsOnly = true) CommandContext<?> context) {
        if (isValid != null) {
            patterns = StreamSupport.stream(patterns.spliterator(), false).filter(s -> isValid.apply(context, s)).toList();
            for (var pat : patterns) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(pat));
            }
        }
        return original.call(patterns, suggestionsBuilder);
    }
}

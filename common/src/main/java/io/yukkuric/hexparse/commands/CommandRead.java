package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("read").executes(ctx -> readHand(ctx, code -> {
                    var p = ctx.getSource().getPlayer();
                    if (p == null) return;
                    var display = Component.literal("Result: ").withStyle(ChatFormatting.GREEN).append(Component.literal(code).withStyle(ChatFormatting.WHITE));
                    p.sendSystemMessage(wrapClickCopy(display, code));
                }))
        );
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, @NotNull Consumer<String> then) {
        var code = CodeHelpers.readHand(ctx.getSource().getPlayer());
        if (code == null) return 0;
        then.accept(code);
        return 1919810;
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code)));
    }
}

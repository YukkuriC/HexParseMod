package io.yukkuric.hexparse_client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse_client.misc.CodeHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static io.yukkuric.hexparse_client.HexParse.Client;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("read").executes(ctx -> readHand(ctx, code -> {
                    var p = Client.player;
                    if (p == null) return;
                    var display = Component.literal("Result: ").withStyle(ChatFormatting.GREEN).append(Component.literal(code).withStyle(ChatFormatting.WHITE));
                    p.sendSystemMessage(wrapClickCopy(display, code));
                }))
        );
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, @NotNull Consumer<String> then) {
        var code = CodeHelpers.readHand(Client.player);
        if (code == null) return 0;
        then.accept(code);
        return 1919810;
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal("CLICK TO COPY")))
        );
    }
}

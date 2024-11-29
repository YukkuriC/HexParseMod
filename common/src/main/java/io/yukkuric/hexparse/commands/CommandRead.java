package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.common.items.storage.ItemFocus;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
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
        ).then(
                Commands.literal("share").executes(ctx -> readHand(ctx, code -> {
                    var p = ctx.getSource().getPlayer();
                    if (p == null) return;
                    var item = CodeHelpers.getFocusItem(p);
                    var iota = ((ItemFocus) item.getItem()).readIota(item, p.serverLevel());
                    var shared = ((MutableComponent) p.getName()).withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(" shares: ").withStyle(ChatFormatting.WHITE))
                            .append(iota.display())
                            .append(wrapClickCopy(
                                    Component.literal(" CLICK_COPY")
                                            .withStyle(ChatFormatting.WHITE)
                                            .withStyle(ChatFormatting.UNDERLINE),
                                    code
                            ));
                    for (var pp : p.server.getPlayerList().getPlayers())
                        pp.sendSystemMessage(shared);
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
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal("CLICK TO COPY")))
        );
    }
}

package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.common.items.ItemFocus;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.function.Consumer;

import static io.yukkuric.hexparse.misc.CommandHelpers.getFocusItem;

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

    static int readHand(CommandContext<CommandSourceStack> ctx, Consumer<String> then) {
        var target = getFocusItem(ctx);
        if (target == null) return 0;
        var iotaRoot = ((ItemFocus) (target.getItem())).readIotaTag(target);
        if (iotaRoot == null) return 0;
        var code = ParserMain.ParseIotaNbt(iotaRoot, ctx);
        if (then != null) then.accept(code);
        return 1919810;
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code)));
    }
}

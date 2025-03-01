package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.common.items.storage.ItemFocus;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.misc.StringProcessors;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("read").executes(ctx -> readHand(ctx, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("read_hexbug").executes(ctx -> readHand(ctx, StringProcessors.READ_HEXBOT_VARIANT, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("share").executes(ctx -> readHand(ctx, code -> {
                    var p = ctx.getSource().getPlayer();
                    if (p == null) return;
                    var item = CodeHelpers.getFocusItem(p);
                    var iota = ((ItemFocus) item.getItem()).readIota(item, p.serverLevel());
                    if (iota == null) return;
                    var shared = Component.translatable("hexparse.cmd.read.share",
                            ((MutableComponent) p.getName()).withStyle(ChatFormatting.GOLD),
                            iota.display(),
                            CodeHelpers.wrapClickCopy(Component.translatable("chat.copy.click"), code)
                    );
                    for (var pp : p.server.getPlayerList().getPlayers())
                        pp.sendSystemMessage(shared);
                }))
        );
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, @NotNull Consumer<String> then) {
        return readHand(ctx, StringProcessors.READ_DEFAULT, then);
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, StringProcessors.F post, @NotNull Consumer<String> then) {
        var code = CodeHelpers.readHand(ctx.getSource().getPlayer(), post);
        if (code == null) return 0;
        then.accept(code);
        return 1919810;
    }
}

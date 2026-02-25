package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.*;
import io.yukkuric.hexparse.parsers.interfaces.ConfigNums;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("read").executes(ctx -> readHand(ctx, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("read_signatures").executes(ctx -> readHand(ctx, ConfigNums.FORCE_SIGNATURES, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("read_hexbug").executes(ctx -> readHand(ctx, 0, StringProcessors.READ_HEXBOT_VARIANT, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("share").executes(ctx -> readHand(ctx, code -> {
                    var p = ctx.getSource().getPlayer();
                    if (p == null) return;
                    var io = CodeHelpersKt.getItemIO(p, false);
                    var iota = io.readIota((ServerLevel) p.level);
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

    static int readHand(CommandContext<CommandSourceStack> ctx, int configNum, @NotNull Consumer<String> then) {
        return readHand(ctx, configNum, StringProcessors.READ_DEFAULT, then);
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, @NotNull Consumer<String> then) {
        return readHand(ctx, 0, StringProcessors.READ_DEFAULT, then);
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, int configNum, StringProcessors.F post, @NotNull Consumer<String> then) {
        var code = CodeHelpers.readHand(ctx.getSource().getPlayer(), configNum, post);
        if (code == null) return 0;
        then.accept(code);
        return 1919810;
    }
}

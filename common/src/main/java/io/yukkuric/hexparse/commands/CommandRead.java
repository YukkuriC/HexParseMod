package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.common.items.ItemFocus;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("read").executes(ctx -> readHand(ctx, code -> CodeHelpers.displayCode(ctx.getSource().getPlayer(), code)))
        ).then(
                Commands.literal("share").executes(ctx -> readHand(ctx, code -> {
                    var p = ctx.getSource().getPlayer();
                    if (p == null) return;
                    var item = CodeHelpers.getFocusItem(p);
                    var iota = ((ItemFocus) item.getItem()).readIota(item, (ServerLevel) p.level);
                    var shared = ((MutableComponent) p.getName()).withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(" shares: ").withStyle(ChatFormatting.WHITE))
                            .append(iota.display())
                            .append(CodeHelpers.wrapClickCopy(
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
}

package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.items.ItemFocus;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

import static io.yukkuric.hexparse.misc.CommandHelpers.*;

public class CommandRead {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        //TODO
    }

    static int readHand(CommandContext<CommandSourceStack> ctx, Consumer<Iota> then) {
        var target = getFocusItem(ctx);
        if (target == null) return 0;
        var iotaRoot = ((ItemFocus) (target.getItem())).readIota(target, ctx.getSource().getLevel());
        if (iotaRoot != null) then.accept(iotaRoot);
        return 1919810;
    }
}

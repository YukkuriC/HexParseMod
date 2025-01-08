package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandLehmerHelper {
    static final int MAX_COUNT = 20;

    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("lehmer").then(
                        Commands.argument("input", StringArgumentType.greedyString())
                                .executes(CommandLehmerHelper::CalcLehmer)
                )
        );
    }

    public static int CalcLehmer(CommandContext<CommandSourceStack> ctx) {
        var raw = StringArgumentType.getString(ctx, "input").split("\\s");
        List<Integer> orders = new ArrayList<>();
        for (var seg : raw) {
            try {
                var num = Integer.valueOf(seg);
                orders.add(num);
                if (orders.size() > MAX_COUNT)
                    throw new IndexOutOfBoundsException(HexParse.doTranslate("hexparse.msg.error.code_too_long", MAX_COUNT));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.unknown_symbol", seg));
            }
        }
        long res = 0, frac = 1;
        for (int offset = 1; offset < orders.size(); offset++) {
            frac *= offset;
            int pos = orders.size() - 1 - offset;
            int cur = orders.get(pos), cnt = 0;
            for (int j = pos; j < orders.size(); j++) if (orders.get(j) < cur) cnt++;
            res += frac * cnt;
        }
        var player = ctx.getSource().getPlayer();
        if (player != null) {
            var resStr = String.valueOf(res);
            CodeHelpers.displayCode(player, resStr);
        }

        return 1;
    }
}

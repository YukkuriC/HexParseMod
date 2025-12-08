package io.yukkuric.hexparse.commands

import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.context.CommandContext
import io.yukkuric.hexparse.hooks.HexParseCommands
import io.yukkuric.hexparse.misc.CodeHelpersKt.Companion.doExtractMedia
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CommandDonate {
    @JvmStatic
    fun init() {
        HexParseCommands.registerLine(
            CommandDonate::doDonate,
            Commands.literal("donate"),
            Commands.argument("amount", LongArgumentType.longArg(1))
        )
    }

    @JvmStatic
    fun doDonate(ctx: CommandContext<CommandSourceStack?>): Int {
        val caster = ctx.source?.player ?: return 0
        val amount = LongArgumentType.getLong(ctx, "amount") * 10000 // as dust unit
        doExtractMedia(caster, amount)
        return 1
    }
}
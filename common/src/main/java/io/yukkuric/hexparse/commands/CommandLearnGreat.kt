package io.yukkuric.hexparse.commands

import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import com.mojang.brigadier.context.CommandContext
import io.yukkuric.hexparse.actions.ActionLearnGreatPatterns
import io.yukkuric.hexparse.config.HexParseConfig.*
import io.yukkuric.hexparse.hooks.HexParseCommands
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.world.InteractionHand

object CommandLearnGreat {
    fun init() {
        HexParseCommands.registerLine(
            ::learnHand,
            Commands.literal("learn_great")
                .requires { canParseGreatPatterns() == ParseGreatPatternMode.BY_SCROLL },
        )
    }

    fun learnHand(ctx: CommandContext<CommandSourceStack?>): Int {
        val caster = ctx.source?.player ?: return 0
        val env = StaffCastEnv(caster, InteractionHand.MAIN_HAND)
        val ret = ActionLearnGreatPatterns.execute(listOf(), env)[0]
        caster.sendSystemMessage(ret.display())
        return ret.size()
    }
}
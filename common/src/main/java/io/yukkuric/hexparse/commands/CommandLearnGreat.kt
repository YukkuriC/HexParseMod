package io.yukkuric.hexparse.commands

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.ListIota
import com.mojang.brigadier.context.CommandContext
import io.yukkuric.hexparse.actions.ActionLearnGreatPatterns
import io.yukkuric.hexparse.config.HexParseConfig.ParseGreatPatternMode
import io.yukkuric.hexparse.config.HexParseConfig.canParseGreatPatterns
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
        val env = CastingContext(caster, InteractionHand.MAIN_HAND, CastingContext.CastSource.STAFF)
        val ret = ListIota(ActionLearnGreatPatterns.execute(listOf(), env))
        caster.sendSystemMessage(ret.display())
        return ret.list.size()
    }
}
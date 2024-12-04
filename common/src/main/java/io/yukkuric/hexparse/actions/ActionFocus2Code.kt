package io.yukkuric.hexparse.actions
//
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import io.yukkuric.hexparse.misc.CodeHelpers

object ActionFocus2Code : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        var player = ctx.caster
        var code = CodeHelpers.readHand(player)
        CodeHelpers.displayCode(player, code)
        return listOf()
    }
}
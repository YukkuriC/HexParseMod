package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import io.yukkuric.hexparse.misc.CodeHelpers

object ActionFocus2Code : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        var player = env.caster
        var code = CodeHelpers.readHand(player)
        CodeHelpers.displayCode(player, code)
        return listOf()
    }
}
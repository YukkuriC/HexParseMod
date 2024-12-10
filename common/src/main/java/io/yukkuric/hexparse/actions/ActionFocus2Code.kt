package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import io.yukkuric.hexparse.misc.CodeHelpers

object ActionFocus2Code : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is StaffCastEnv) throw MishapDisallowedSpell()
        var player = env.caster
        var code = CodeHelpers.readHand(player)
        CodeHelpers.displayCode(player, code)
        return listOf()
    }
}
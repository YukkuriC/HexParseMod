package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedMishapEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import io.yukkuric.hexparse.network.MsgHandlers
import io.yukkuric.hexparse.network.MsgPullClipboard

object ActionCode2Focus : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is StaffCastEnv) throw MishapDisallowedSpell()
        MsgHandlers.SERVER.sendPacketToPlayer(env.caster, MsgPullClipboard(null, false))
        return listOf()
    }
}
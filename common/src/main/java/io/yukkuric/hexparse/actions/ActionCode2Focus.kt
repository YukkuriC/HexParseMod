package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import io.yukkuric.hexparse.network.MsgHandlers
import io.yukkuric.hexparse.network.MsgPullClipboard

object ActionCode2Focus : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        if (ctx.source != CastingContext.CastSource.STAFF) throw MishapDisallowedSpell()
        MsgHandlers.SERVER.sendPacketToPlayer(ctx.caster, MsgPullClipboard(null, false))
        return listOf()
    }
}
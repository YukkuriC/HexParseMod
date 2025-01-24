package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.ParserMain
import ram.talia.moreiotas.api.getString

object ActionCompile : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.CRYSTAL_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val data = args.getString(0)
        var tag = ParserMain.ParseCode(data, ctx.caster)
        var iota = HexIotaTypes.deserialize(tag, ctx.world) as ListIota
        return iota.list.asActionResult
    }
}
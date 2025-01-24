package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.misc.MediaConstants
import io.yukkuric.hexparse.hooks.CommentIota
import io.yukkuric.hexparse.misc.CodeHelpers
import io.yukkuric.hexparse.parsers.ParserMain
import ram.talia.moreiotas.api.casting.iota.StringIota
import ram.talia.moreiotas.api.getString

object ActionCompile : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.CRYSTAL_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val data = args.getString(0)
        var tag = ParserMain.ParseCode(data, env.caster)
        var iota = IotaType.deserialize(tag, env.world) as ListIota
        return iota.list.asActionResult
    }
}
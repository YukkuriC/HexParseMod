package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.IotaFactory

object ActionCreateLineBreak : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val n = args.getInt(0)
        var nbt = IotaFactory.makeTab(n)
        return listOf(HexIotaTypes.deserialize(nbt, ctx.world))
    }
}
package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import io.yukkuric.hexparse.parsers.IotaFactory

object ActionCreateLineBreak : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val n = args.getInt(0)
        var nbt = IotaFactory.makeTab(n)
        return listOf(IotaType.deserialize(nbt, env.world))
    }
}
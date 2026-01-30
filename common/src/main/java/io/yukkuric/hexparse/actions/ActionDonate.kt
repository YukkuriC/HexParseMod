package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import kotlin.math.abs

object ActionDonate : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val dusts = abs(args.getDouble(0))
        return SpellAction.Result(
            FooSpell,
            (dusts * MediaConstants.DUST_UNIT).toLong(),
            listOf()
        )
    }

    object FooSpell : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
    }
}
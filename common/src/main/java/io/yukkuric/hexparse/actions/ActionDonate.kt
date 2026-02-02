package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getDouble
import at.petrak.hexcasting.api.spell.iota.Iota
import kotlin.math.abs

object ActionDonate : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val dusts = abs(args.getDouble(0))
        return Triple(
            FooSpell,
            (dusts * MediaConstants.DUST_UNIT).toInt(),
            listOf()
        )
    }

    object FooSpell : RenderedSpell {
        override fun cast(ctx: CastingContext) {}
    }
}
package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import io.yukkuric.hexparse.hooks.CommentIota
import ram.talia.moreiotas.api.spell.iota.StringIota


object ActionCommentSwitcher : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val iota = args[0]
        return when (iota) {
            is StringIota -> listOf(CommentIota(iota.string))
            is CommentIota -> listOf(StringIota(iota.comment))
            else -> listOf(CommentIota(iota.display().string))
        }
    }
}
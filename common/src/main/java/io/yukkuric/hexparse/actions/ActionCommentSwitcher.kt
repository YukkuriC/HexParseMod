package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import io.yukkuric.hexparse.hooks.CommentIota
import ram.talia.moreiotas.api.casting.iota.StringIota

object ActionCommentSwitcher : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val iota = args[0]
        return when (iota) {
            is StringIota -> listOf(CommentIota(iota.string))
            is CommentIota -> listOf(StringIota.make(iota.comment))
            else -> listOf(CommentIota(iota.display().string))
        }
    }
}
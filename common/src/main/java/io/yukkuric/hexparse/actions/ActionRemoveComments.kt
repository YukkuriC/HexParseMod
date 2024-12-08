package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.SpellList
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getList
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import io.yukkuric.hexparse.hooks.CommentIota
import io.yukkuric.hexparse.hooks.CommentIotaType

object ActionRemoveComments : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getList(0)
        return filterComments(target).asActionResult
    }

    private fun filterComments(target: SpellList): SpellList {
        val res = ArrayList<Iota>()
        for (sub in target) {
            if (sub is ListIota) {
                res.add(ListIota(filterComments(sub.list)))
            } else if (sub is CommentIota) continue
            else if (sub is PatternIota && sub.pattern.sigsEqual(CommentIotaType.COMMENT_PATTERN)) continue
            res.add(sub)
        }
        return SpellList.LList(res)
    }
}
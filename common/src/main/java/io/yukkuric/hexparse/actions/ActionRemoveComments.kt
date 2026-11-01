package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.utils.TreeList
import io.yukkuric.hexparse.hooks.CommentIota
import io.yukkuric.hexparse.hooks.CommentIotaType

object ActionRemoveComments : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getList(0)
        return filterComments(target).asActionResult
    }

    private fun filterComments(target: TreeList<Iota>): TreeList<Iota> {
        val res = ArrayList<Iota>()
        for (sub in target) {
            if (sub is ListIota) {
                res.add(ListIota(filterComments(sub.list)))
                continue
            } else if (sub is CommentIota) continue
            else if (sub is PatternIota && sub.pattern.signature == CommentIotaType.COMMENT_PATTERN.signature) continue
            res.add(sub)
        }
        return TreeList.from(res)
    }
}
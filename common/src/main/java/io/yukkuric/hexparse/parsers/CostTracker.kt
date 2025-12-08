package io.yukkuric.hexparse.parsers

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import io.yukkuric.hexparse.misc.CodeHelpersKt
import net.minecraft.server.level.ServerPlayer

object CostTracker : AutoCloseable {
    var player: ServerPlayer? = null
    var totalCost: Long = 0
    var usedCastingEnv: CastingEnvironment? = null

    fun beginTrack(target: ServerPlayer): CostTracker {
        totalCost = 0
        player = target
        return this
    }

    fun addCost(cost: Int) {
        totalCost += cost
    }

    override fun close() {
        if (player == null || totalCost <= 0) return
        totalCost = CodeHelpersKt.doExtractMedia(player, totalCost, usedCastingEnv)
        usedCastingEnv = null
    }
}
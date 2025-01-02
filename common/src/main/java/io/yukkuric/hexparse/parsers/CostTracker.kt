package io.yukkuric.hexparse.parsers

import io.yukkuric.hexparse.misc.CodeHelpers
import net.minecraft.server.level.ServerPlayer

object CostTracker : AutoCloseable {
    var player: ServerPlayer? = null
    var totalCost: Long = 0

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
        CodeHelpers.doExtractMedia(player, totalCost)
    }
}
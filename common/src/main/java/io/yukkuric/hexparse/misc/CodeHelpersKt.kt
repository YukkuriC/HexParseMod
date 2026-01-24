package io.yukkuric.hexparse.misc

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

class CodeHelpersKt {
    companion object {
        @JvmStatic
        fun doExtractMedia(caster: ServerPlayer?, amount: Long, env: CastingEnvironment? = null): Long {
            val env = env ?: IXplatAbstractions.INSTANCE.getStaffcastVM(caster, InteractionHand.MAIN_HAND).env
            return env.extractMedia(amount, false)
        }

        @JvmStatic
        fun getItemIO(player: ServerPlayer?): IOMethod? {
            if (player == null) return null
            val mainHandIO = IOMethod.get(player.mainHandItem)
            val offhandIO = IOMethod.get(player.offhandItem) ?: return mainHandIO
            if (mainHandIO == null || offhandIO.priority <= mainHandIO.priority) return offhandIO // includes same IO, bound to offhand already
            return mainHandIO
        }
    }
}

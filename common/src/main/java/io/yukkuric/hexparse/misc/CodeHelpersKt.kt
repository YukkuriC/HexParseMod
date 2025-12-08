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
    }
}

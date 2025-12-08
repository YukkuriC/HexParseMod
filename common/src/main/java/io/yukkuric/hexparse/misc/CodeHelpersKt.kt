package io.yukkuric.hexparse.misc

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers
import at.petrak.hexcasting.api.misc.DiscoveryHandlers
import at.petrak.hexcasting.api.misc.HexDamageSources
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexStatistics
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.mishaps.Mishap.Companion.trulyHurt
import at.petrak.hexcasting.api.utils.compareMediaItem
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import kotlin.math.max
import kotlin.math.min

class CodeHelpersKt {
    companion object {
        fun doExtractMedia(caster: ServerPlayer, amount: Int, env: CastingContext? = null): Int {
            val harness = if (env == null) IXplatAbstractions.INSTANCE.getHarness(caster, InteractionHand.MAIN_HAND)
            else CastingHarness(env)
            if (env?.spellCircle != null) {
                return harness.withdrawMedia(amount, false)
            }

            // picked from CastingHarness.withdrawMedia
            // https://github.com/FallingColors/HexMod/blob/1.19/Common/src/main/java/at/petrak/hexcasting/api/spell/casting/CastingHarness.kt#L548-L575
            var costLeft = amount
            val mediaSources = DiscoveryHandlers.collectMediaHolders(harness)
                .sortedWith(Comparator(::compareMediaItem).reversed())
            for (source in mediaSources) {
                costLeft -= extractMedia(source, costLeft, simulate = false)
                if (costLeft <= 0)
                    break
            }

            if (costLeft > 0) {
                // Cast from HP!
                val mediaToHealth = HexConfig.common().mediaToHealthRate()
                val healthToRemove = max(costLeft.toDouble() / mediaToHealth, 0.5)
                val mediaAbleToCastFromHP = harness.ctx.caster.health * mediaToHealth

                val mediaToActuallyPayFor = min(mediaAbleToCastFromHP.toInt(), costLeft)

                trulyHurt(harness.ctx.caster, HexDamageSources.OVERCAST, healthToRemove.toFloat())

                val actuallyTaken = Mth.ceil(mediaAbleToCastFromHP - (harness.ctx.caster.health * mediaToHealth))

                HexAdvancementTriggers.OVERCAST_TRIGGER.trigger(harness.ctx.caster, actuallyTaken)
                harness.ctx.caster.awardStat(HexStatistics.MEDIA_OVERCAST, amount - costLeft)
                costLeft -= actuallyTaken
            }
            return costLeft
        }
    }
}
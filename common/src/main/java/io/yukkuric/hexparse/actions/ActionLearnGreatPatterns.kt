package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidPattern
import at.petrak.hexcasting.xplat.IXplatAbstractions
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker
import io.yukkuric.hexparse.hooks.PatternMapper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import java.util.ArrayList

object ActionLearnGreatPatterns : ConstMediaAction {
    override val argc = 0

    lateinit var cachedLevel: ServerLevel
    lateinit var cachedUnlocker: GreatPatternUnlocker

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val player = ctx.caster
        val level = ctx.world
        cachedLevel = level
        cachedUnlocker = GreatPatternUnlocker.get(level)
        if (player == null) throw MishapDisallowedSpell()

        val returns = ArrayList<PatternIota>()
        var item = player.getItemInHand(ctx.otherHand)
        var targetIotas: List<Iota>?
        if (!item.isEmpty) {
            targetIotas = extractTargetFromItem(item)
            if (targetIotas != null) processIotaList(targetIotas, returns)
        }
        item = player.getItemInHand(ctx.castingHand)
        if (!item.isEmpty) {
            targetIotas = extractTargetFromItem(item)
            if (targetIotas != null) processIotaList(targetIotas, returns)
        }
        return returns.asActionResult
    }

    private fun extractTargetFromItem(item: ItemStack): List<Iota>? {
        val holderIota = IXplatAbstractions.INSTANCE.findDataHolder(item)
        if (holderIota != null) {
            val inner = holderIota.readIota(cachedLevel)
            if (inner != null) return listOf(inner)
        }
        val holderHex = IXplatAbstractions.INSTANCE.findHexHolder(item)
        if (holderHex != null) return holderHex.getHex(cachedLevel)
        return null
    }

    private fun processIotaList(seq: Iterable<Iota>, output: ArrayList<PatternIota>) {
        for (iota in seq) {
            if (iota is ListIota) processIotaList(iota.list, output)
            else processIota(iota, output)
        }
    }

    private fun processIota(target: Iota, output: ArrayList<PatternIota>) {
        if (target !is PatternIota) return
        val key = fetchPatternGreatKey(target.pattern) ?: return
        if (output.none { it.toleratesOther(target) }) {
            // do unlock
            if (cachedUnlocker.unlock(key.toString())) output.add(target)
        }
    }

    private fun fetchPatternGreatKey(pattern: HexPattern): ResourceLocation? {
        try {
            val match = PatternRegistry.matchPatternAndID(pattern, cachedLevel).second
            if (PatternMapper.greatMapper.containsKey(match)) return match
            return null
        } catch (e: MishapInvalidPattern) {
            return null
        }
    }
}
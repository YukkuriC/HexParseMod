package io.yukkuric.hexparse.actions

import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.xplat.IXplatAbstractions
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack

object ActionLearnGreatPatterns : ConstMediaAction {
    override val argc = 0

    lateinit var cachedEnv: CastingEnvironment
    lateinit var cachedLevel: ServerLevel
    lateinit var cachedUnlocker: GreatPatternUnlocker

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        cachedEnv = env
        val player = env.caster
        val level = env.world
        cachedLevel = level
        cachedUnlocker = GreatPatternUnlocker.get(level)
        if (player == null) throw MishapDisallowedSpell()

        val returns = ArrayList<PatternIota>()
        var item = player.getItemInHand(env.otherHand)
        var targetIotas: List<Iota>?
        if (!item.isEmpty) {
            targetIotas = extractTargetFromItem(item)
            if (targetIotas != null) processIotaList(targetIotas, returns)
        }
        item = player.getItemInHand(env.castingHand)
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
        var matcher = PatternRegistryManifest.matchPattern(pattern, cachedEnv, false)
        if (matcher !is PatternShapeMatch.PerWorld) return null
        return matcher.key.location()
    }
}
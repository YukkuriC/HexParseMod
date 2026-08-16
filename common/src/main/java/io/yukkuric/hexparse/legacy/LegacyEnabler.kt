package io.yukkuric.hexparse.legacy

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import net.minecraft.server.level.ServerPlayer

val CastingEnvironment.caster: ServerPlayer?
    get() = castingEntity as? ServerPlayer

fun MishapDisallowedSpell() = MishapDisallowedSpell("disallowed", null)

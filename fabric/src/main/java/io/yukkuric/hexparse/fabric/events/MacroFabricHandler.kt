package io.yukkuric.hexparse.fabric.events

import io.yukkuric.hexparse.macro.MacroClientHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

class MacroFabricHandler : MacroClientHandler() {
    companion object {
        fun init() {
            ClientPlayConnectionEvents.JOIN.register { _, _, _ -> load() }
            ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> load() }
        }
    }
}
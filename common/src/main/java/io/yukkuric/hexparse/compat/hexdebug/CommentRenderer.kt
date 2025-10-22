package io.yukkuric.hexparse.compat.hexdebug

import gay.`object`.hexdebug.api.client.splicing.SplicingTableIotaRenderer
import gay.`object`.hexdebug.api.client.splicing.SplicingTableIotaTooltipBuilder
import gay.`object`.hexdebug.api.splicing.SplicingTableIotaClientView
import io.yukkuric.hexparse.hooks.CommentIotaType
import net.minecraft.client.gui.GuiGraphics

class CommentRenderer(iota: SplicingTableIotaClientView, x: Int, y: Int) :
    SplicingTableIotaRenderer(CommentIotaType.INSTANCE, iota, x, y) {
    override fun render(p0: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        // nope
    }
}